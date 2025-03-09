import argparse

import models
import torch
import torchvision.models as torchvision_models
import wandb
from modules.datasets import CombinedDataset
from modules.losses import get_loss
from modules.optimizers import get_optimizer
from modules.schedulers import get_scheduler
from modules.trainer import Trainer
from modules.utils import dir_set, fix_seeds, load_yaml, setup_cudnn
from torch.amp import GradScaler
from torch.utils.data import DataLoader


def parse_args():
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "--cfg", type=str, required=True, help="Configuration file to use"
    )
    parser.add_argument("--resume", action="store_true", help="Resume training")
    args = parser.parse_args()

    return args


def load_model(model_name, num_classes, pretrained):
    if hasattr(models, model_name):
        return getattr(models, model_name)(
            num_classes=num_classes, pretrained=pretrained
        )
    else:
        return getattr(torchvision_models, model_name)(
            num_classes=num_classes, pretrained=pretrained
        )


def main(cfg, resume=False):
    device = torch.device(cfg["DEVICE"])

    GROUP_NAME = "experiment-" + wandb.util.generate_id()

    fix_seeds(cfg["RANDOM_SEED"])
    setup_cudnn()

    for fold_idx in range(cfg["DATASET"]["N_FOLDS"]):
        wandb.init(
            project=cfg["WANDB_PROJECT"],
            name=f"fold-{fold_idx}",
            group=GROUP_NAME,
            job_type="train",
            config=cfg,
            reinit=True,
        )

        train_set, val_set = CombinedDataset(cfg["DATASET"]["TRAIN_DATA_DIR"])

        train_loader = DataLoader(
            train_set,
            batch_size=cfg["TRAIN"]["BATCH_SIZE"],
            num_workers=cfg["DATASET"]["NUM_WORKERS"],
            shuffle=True,
            pin_memory=True,
        )

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
        model = model.to(device)
        criterion = get_loss(cfg["LOSS"]["NAME"], label_smoothing=0.1)
        optimizer = get_optimizer(model, cfg["OPTIMIZER"])
        scheduler = get_scheduler(optimizer, **cfg["SCHEDULER"])
        scaler = GradScaler()

        save_dir, name = dir_set(cfg["SAVE_DIR"], model)

        trainer = Trainer(
            model=model,
            criterion=criterion,
            optimizer=optimizer,
            scheduler=scheduler,
            scaler=scaler,
            config=cfg,
            device=device,
            wandb=wandb,
            checkpoint_dir=save_dir,
            fold_idx=fold_idx,
            train_loader=train_loader,
            valid_loader=valid_loader,
            resume=resume,
        )

        trainer.train()
        wandb.run.finish()

    wandb.finish()


if __name__ == "__main__":
    args = parse_args()
    cfg = load_yaml(args.cfg)

    main(cfg, args.resume)
