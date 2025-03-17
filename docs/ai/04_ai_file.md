# AI File

이 문서에서는 AI Pre-trained Weights 에 대해 설명합니다.

[처음으로](../overview.md) |
[소개로](00_introduction.md) |
[이전: 모델 평가](03_ai_evaluation.md)
[다음: Pre-trained Weights](01_ai_file.md) |

# 소개
학습에 사용된 데이터와 pre-trained weights 의 링크를 설명합니다.

# Dataset
[Hugging-face Link](https://huggingface.co/datasets/a-eyelab/cataract-train)

# Pre-trained weights
[Hugging-face Link](https://huggingface.co/a-eyelab/cataract)

## 사용법
### Model train
```
python src/train.py --cfg src/config/train.yaml
```

### Model evaluation
```
python src/eval.py --dataset_path /dataset/{your_dataset or kaggle_cataract_nand(default)}
```