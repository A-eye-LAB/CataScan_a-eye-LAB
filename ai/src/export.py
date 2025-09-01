import argparse
import os

import models
import onnx
import torch
import torch.onnx
import torchvision.models as torchvision_models
from modules.utils import load_yaml
from onnxruntime.quantization import QuantType, quantize_dynamic


def parse_args():
    parser = argparse.ArgumentParser()
    parser.add_argument("--cfg", type=str, required=True, help="Configuration file to use")
    parser.add_argument("--ckpt", type=str, required=True, help="Checkpoint file to load model weights")
    parser.add_argument("--onnx_path", type=str, default="models/", help="Path to save ONNX model")

    args = parser.parse_args()

    return args


def load_model(model_name, num_classes, pretrained):
    if hasattr(models, model_name):
        return getattr(models, model_name)(num_classes=num_classes, pretrained=pretrained)
    else:
        return getattr(torchvision_models, model_name)(num_classes=num_classes, pretrained=pretrained)


def export_to_onnx(model, input_shape, save_path, device):
    """PyTorch 모델을 ONNX로 변환"""
    model.eval()
    dummy_input = torch.randn(input_shape).to(device)

    torch.onnx.export(
        model,
        dummy_input,
        save_path,
        export_params=True,
        opset_version=18,
        do_constant_folding=True,
        input_names=["input"],
        output_names=["output1", "output2"],
        dynamic_axes={"input": {0: "batch_size"}, "output1": {0: "batch_size"}, "output2": {0: "batch_size"}},
        training=torch.onnx.TrainingMode.EVAL,
        keep_initializers_as_inputs=True,
    )
    print(f"ONNX 모델이 {save_path}에 저장되었습니다.")


def quantize_onnx_model(onnx_path, quantized_path):
    """ONNX 모델 동적 양자화"""
    quantized_model = quantize_dynamic(
        model_input=onnx_path,
        model_output=quantized_path,
        weight_type=QuantType.QUInt8,
        per_channel=False,
    )
    print(f"양자화된 모델이 {quantized_path}에 저장되었습니다.")


def verify_onnx_model(onnx_path):
    """ONNX 모델 검증"""
    model = onnx.load(onnx_path)
    onnx.checker.check_model(model)
    print("ONNX 모델 검증이 완료되었습니다.")


if __name__ == "__main__":
    args = parse_args()

    cfg = load_yaml(args.cfg)
    device = torch.device(cfg["DEVICE"])
    model = load_model(
        cfg["MODEL"]["NAME"],
        cfg["MODEL"]["NUM_CLASSES"],
        cfg["MODEL"]["PRETRAINED"],
    )
    model.load(args.ckpt)
    model = model.to(device)

    model.eval()
    input_shape = (1, 3, 224, 224)  # 예시 입력 shape

    # ONNX 변환
    onnx_path = os.path.join(args.onnx_path, "./model.onnx")
    export_to_onnx(model, input_shape, onnx_path, device)

    # 모델 검증
    verify_onnx_model(onnx_path)

    # 양자화
    quantized_path = os.path.join(args.onnx_path, "./model_quantized.onnx")
    quantize_onnx_model(onnx_path, quantized_path)
