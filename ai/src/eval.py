import argparse
import os

import models
import torch
import torchvision.models as torchvision_models
import torchvision.transforms as transforms
from modules.losses import get_loss
from modules.metrics import evaluate_model, print_evaluation
from modules.utils import load_yaml
from PIL import Image
from torch.utils.data import DataLoader, Dataset
from tqdm import tqdm


def parse_args():
    parser = argparse.ArgumentParser()
    parser.add_argument("--cfg", type=str, required=True, help="Configuration file to use")
    parser.add_argument("--ckpt", type=str, required=True, help="Checkpoint file to load model weights")
    parser.add_argument(
        "--dataset_path",
        type=str,
        default="dataset/data/kaggle_cataract_nand",
        help="test dataset path",
    )

    args = parser.parse_args()

    return args


class CustomImageDataset(Dataset):
    def __init__(self, dataset_path, transform=None):
        """
        커스텀 데이터셋 클래스 (이미지와 레이블을 로드)
        """
        self.dataset_path = dataset_path
        self.transform = transform
        self.data = []
        self.labels = []
        self._load_data()

    def _load_data(self):
        """
        디렉토리에서 라벨이 된 이미지 로드
        디렉토리 구조
        dataset
          ㄴ 0      : Nomal
          ㄴ 1      : Cataract
        """
        print(f"Loading images from: {self.dataset_path}")
        classes = {"0": 0, "1": 1}

        for label_dir, label in classes.items():
            class_path = os.path.join(self.dataset_path, label_dir)
            if not os.path.isdir(class_path):
                raise ValueError(f"Directory '{class_path}' does not exist.")

            for file_name in os.listdir(class_path):
                file_path = os.path.join(class_path, file_name)
                try:
                    image = Image.open(file_path).convert("RGB")
                    if self.transform:
                        image = self.transform(image)
                    self.data.append(image)
                    self.labels.append(label)
                except Exception as e:
                    print(f"Failed to process image: {file_path}. Error: {e}")

    def __len__(self):
        return len(self.data)

    def __getitem__(self, idx):
        return self.data[idx], self.labels[idx]


def load_model(model_name, num_classes, pretrained):
    if hasattr(models, model_name):
        return getattr(models, model_name)(num_classes=num_classes, pretrained=pretrained)
    else:
        return getattr(torchvision_models, model_name)(num_classes=num_classes, pretrained=pretrained)


def main(args):
    cfg = load_yaml(args.cfg)

    device = torch.device(cfg["DEVICE"])

    norm = {"mean": (0.485, 0.456, 0.406), "std": (0.229, 0.224, 0.225)}
    transform = transforms.Compose(
        [
            transforms.Resize(224),
            transforms.CenterCrop((224, 224)),
            transforms.ToTensor(),
            transforms.Normalize(**norm),
        ]
    )
    val_set = CustomImageDataset(args.dataset_path, transform)

    valid_loader = DataLoader(
        val_set,
        batch_size=cfg["TRAIN"]["BATCH_SIZE"],
        num_workers=cfg["DATASET"]["NUM_WORKERS"],
        shuffle=False,
        pin_memory=True,
    )

    model = load_model(
        cfg["MODEL"]["NAME"],
        cfg["MODEL"]["NUM_CLASSES"],
        cfg["MODEL"]["PRETRAINED"],
    )
    model.load(args.ckpt)
    model = model.to(device)
    criterion = get_loss(cfg["LOSS"]["NAME"])

    model.eval()
    total_loss = 0.0
    y_true = []
    y_pred = []

    for data, target in tqdm(valid_loader, desc="Validation", leave=False):
        data = data.to(device, non_blocking=True)
        target = target.to(device, non_blocking=True)

        output = model(data)
        loss = criterion(output, target)

        total_loss += loss.item()
        _, predicted = torch.max(output, 1)

        y_true.extend(target.cpu().numpy())
        y_pred.extend(predicted.cpu().numpy())

    valid_loss = total_loss / len(valid_loader)
    metrics = evaluate_model(y_true, y_pred)
    print_evaluation("validate", 1, valid_loss, metrics)


if __name__ == "__main__":
    args = parse_args()

    main(args)
