# CataScan AI

AI 모델 학습 및 추론 코드입니다.

## Download Dataset
```bash
./dataset/download_dataset.bash
```
## Model Train
```bash
python src/train.py --cfg src/config/train.yaml
```
## Model Evaluation (with extern dataset)
```bash
python src/eval.py --dataset_path /dataset/{your_dataset or kaggle_cataract_nand(default)}
```

자세한 내용은 [AI 모델 개요](../docs/ai/00_introduction.md)를 참고해주세요. 