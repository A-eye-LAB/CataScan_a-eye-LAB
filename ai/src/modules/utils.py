import random
from datetime import datetime
from pathlib import Path
from typing import Any, Tuple, Union

import numpy as np
import torch
import yaml
from torch.backends import cudnn


# 시드값 고정 함수
def fix_seeds(seed: int = 42) -> None:
    torch.manual_seed(seed)
    torch.cuda.manual_seed(seed)
    np.random.seed(seed)
    random.seed(seed)


# CUDNN 설정 함수
def setup_cudnn() -> None:
    cudnn.benchmark = True
    cudnn.deterministic = False
    cudnn.enabled = True


# 모델 저장 디렉토리 설정 함수
def dir_set(save_dir: Union[str, Path], model: torch.nn.Module) -> Tuple[Path, str]:
    save_dir = Path(save_dir)
    name = f"{type(model).__name__}_{datetime.now().strftime('%Y%m%d_%H%M%S')}"
    final_save_dir = save_dir / name
    final_save_dir.mkdir(parents=True, exist_ok=True)

    return final_save_dir, name


# YAML 파일 로드 함수
def load_yaml(yaml_path: Union[str, Path]) -> Any:
    with open(yaml_path, "r") as f:

        return yaml.safe_load(f)


def map_dict_to_str(config: dict[str, Any]) -> str:
    config_str = ", ".join(
        f"{key}: {value}"
        for key, value in config.items()
        if key not in ["dataset", "epochs", "batch_size"]
    )
    return config_str
