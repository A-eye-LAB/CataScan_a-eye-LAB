# CataScan AI

카메라로 촬영한 눈 이미지를 이용하여 백내장 유무를 분류하는 AI 모델 관련 코드입니다. 데이터 전처리, 모델 학습, 평가 및 ONNX 변환과 관련된 코드가 포함되어 있습니다.

자세한 내용은 [AI 모델 개요](../docs/ai/00_introduction.md)를 참고해주세요. 

## 1. 기술 스택
- **프로그래밍 언어**: Python
- **프레임워크**: PyTorch
- **모델 변환**: ONNX


## 2. 폴더 구조
```
A-Eye-Lab-Research/
├── dataset/       # 데이터셋 관련 코드
├── src/         # 학습 관련 코드
│   ├── config/    # 학습 관련 config
│   ├── models/    # 모델 코드
│   ├── modules/   # 모델 학습 관련 코드 (optimizer, scheduler 등)
│   ├── eval.py    # 평가 코드
│   ├── train.py   # 학습 코드
├── requirements.txt  # 필수 패키지 목록
├── README.md      # 프로젝트 설명 파일
```

## 3. Setup
```bash
pip install -r requirements.txt
```

## 4. Usage
### Download Dataset
```bash
chmod +x ./dataset/download_dataset.bash

./dataset/download_dataset.bash dataset/data/
```

### Model Train
```bash
python src/train.py --cfg src/config/train.yaml
```
- config 각 파라미터에 대한 설명은 하단의 "5. config 설정"을 참고해주세요.

### Model Evaluation (with external dataset)
```bash
python src/eval.py \
    --dataset_path /dataset/data/kaggle_cataract_nand \
    --ckpt best_checkpoint_path \
    --cfg src/config/train.yaml
```

### Export ONNX Model
```bash
python src/export.py \
    --ckpt best_checkpoint_path \
    --cfg src/config/train.yaml \
    --onnx_path models/

# ONNX 모델 평가
python src/onnx_eval.py \
    --onnx_path models/model_quantized.onnx \
    --dataset_path dataset/data/kaggle_cataract_nand
```

## 5. config 설정
- `src/config/train.yaml` 파일을 수정하여 학습에 필요한 설정을 변경할 수 있습니다.
- config 주요 항목에 대한 설명은 다음과 같습니다.
    - `DEVICE`: 사용할 디바이스 (cpu/cuda/mps 등)
    - `RANDOM_SEED`: 랜덤 시드
    - `MODEL`
        - `NAME`: 사용할 모델 이름
        - `NUM_CLASSES`: 클래스 개수 (binary classification이므로 2)
        - `PRETRAINED`: 사전 학습된 모델 사용 여부
    - `DATASET`
        - `TRAIN_DATA_DIR`: 학습에 사용할 데이터셋 경로 목록
        - `NUM_WORKERS`: 데이터 로드에 사용할 worker 수
        - `N_FOLDS`: K-Fold Cross Validation을 위한 Fold 수. 1로 설정시 사용하지 않음
    - `TRAIN`
        - `BATCH_SIZE`: 배치 사이즈
        - `EPOCHS`: 학습 에폭 수
        - `PATIENCE`: Early Stopping을 위한 patience 값
        - `AMP`: Automatic Mixed Precision 사용 여부
    - `LOSS`
        - `NAME`: 사용할 Loss 함수 이름
    - `OPTIMIZER`
        - `NAME`: 사용할 Optimizer 이름
        - `lr`: Learning Rate
        - `weight_decay`: Weight Decay
    - `SCHEDULER`
        - `scheduler_name`: 사용할 Scheduler 이름
        - `mode`: Scheduler mode
        - `factor`: Scheduler factor
        - `patience`: Scheduler patience
        - `min_lr`: Scheduler 최소 Learning Rate
    - `SAVE_DIR`: 학습 결과 저장 경로
    - `WANDB_PROJECT`: Weights & Biases 프로젝트 이름