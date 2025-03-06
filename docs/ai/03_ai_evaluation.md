# AI 모델 평가

이 문서에서는 AI 모델의 성능 평가 방법과 결과에 대해 설명합니다.

[처음으로](../overview.md) |
[소개로](00_introduction.md) |
[이전: 데이터](02_ai_data.md)

# 서론
## 모델 평가의 목적
모델 평가는 Cataract Classifier 및 Eye Verification 모델이 실제 환경에서 안정적이고 신뢰할 수 있는 성능을 제공하는지 확인하기 위해 수행됩니다. 이를 통해 모델의 강점과 한계를 명확히 분석하고, 향후 개선 방향을 도출하는 것이 목표입니다.

## 평가 방법 개요
모델 평가는 정량적 평가(수치 기반 성능 측정)와 정성적 평가(결과 해석 및 한계점 분석) 두 가지 관점에서 진행됩니다. 이를 통해 모델의 성능을 다각도로 분석하고, 실제 환경에서 신뢰할 수 있는지 검증합니다.

# 정량적 평가 지표
모델의 객관적인 성능을 측정하기 위해 Accuracy, Precision, Recall, F1 Score 등 주요 성능 지표를 활용합니다.

1. 정확도 (Accuracy):전체 샘플 중 모델이 올바르게 분류한 비율을 의미합니다.

    $ Accuracy = \frac{TP + TN} {TP+TN+FP+FN} $
    - TP: True Positive (정상 눈 예측이 맞음)
    - TN: True Negative (비정상 눈 예측이 맞음)
    - FP: False Positive (비정상 눈을 정상 눈으로 잘못 예측)
    - FN: False Negative (정상 눈을 비정상 눈으로 잘못 예측)

2.  정밀도 (Precision)
    - 모델이 정상 눈이라고 예측한 것 중 실제로 정상 눈인 비율을 의미합니다.
    - 높은 정밀도는 모델이 잘못된 긍정 예측을 최소화 하고 있음을 나타냅니다.

    $ Precision = \frac{TP}{TP+FP} $

3. 재현율 (Recall)
    - 실제 정상 눈 중에서 모델이 정상 눈이라고 정확히 예측한 비율을 의미합니다.
    - 높은 재현율은 모델이 가능한 한 많은 정상 눈을 올바르게 예측하고 있음을 나타냅니다.

    $ Recall = \frac{TP}{TP+FN}$

4. F1-Score
    - 정밀도와 재현율의 조화 평균입니다. 불균형 데이터셋에서 유용하게 사용됩니다.
    - F1-Score는 정밀도와 재현율 사이의 균형을 맞추는 데 유용한 지표입니다.

    $ F1Score = \frac{Precision x Recall}{Precision + Recall}$

5. AUC-ROC (Area Under the Curve - Receiver Operating Characteristic)
    - ROC Curve는 모델의 민감도 (True Positive Rate)와 1 - 특이도 (False Positive Rate)를 그래프로 나타낸 것입니다.
    - AUC (Area Under the Curve)**는 ROC 곡선 아래의 면적을 측정하여 모델의 성능을 평가합니다. AUC 값은 0과 1 사이이며, 값이 1에 가까울수록 모델 성능이 뛰어납니다.
    - AUC 값이 0.5 이하이면 모델의 예측 성능이 무작위 추측 수준에 있다는 의미입니다.

# 모델 성능 분석
## 테스트 결과 및 분석
### Eye Verification 정확도
백내장 이미지 37장, 일반 이미지 130장을 이용한 정확도 측정

| Precision | Recall | F1 Score | Accuracy | Specificity |
|--|--|--|--|--|
|0.8780| 0.9231| 0.9000| 0.9223| 0.9219|

### Cataract Classifier 정확도
| Precision | Recall | F1 Score | Accuracy | Specificity |
|--|--|--|--|--|
| 0.9695| 0.9578| 0.9636| 0.9718| 0.9808|

# 정성적 평가
## 결과 해석 및 의미
### Cataract Classifier 모델

- 모델이 정상(Healthy)과 백내장(Cataract)을 얼마나 잘 구분하는지 확인합니다.
- 예측이 **높은 확신(Confidence Score)**을 가지는 경우와 낮은 경우를 비교하여 신뢰성을 평가합니다.


|예측 결과| 의미| 실제 활용시 고려 할 점|
|--|--|--|
|정상 (Healthy) → 정상 (정확한 예측)| 모델이 정상적인 눈을 잘 인식함| 신뢰할 수 있는 예측|
|백내장 (Cataract) → 백내장 (정확한 예측)| 백내장 여부를 잘 감지함| 신뢰할 수 있는 예측|
|정상 → 백내장 (False Positive, FP)| 정상적인 눈을 백내장으로 잘못 판단| 의료적 부담 증가, 추가 진단 필요|
|백내장 → 정상 (False Negative, FN)| 백내장을 정상으로 잘못 판단| 치명적 오류 가능, 재검토 필요|

### Eye Verification 모델
- 입력된 눈이 사람의 눈인지 아닌지를 판별하는 모델이므로, 다양한 환경에서의 예측을 검토해야 합니다.
- 조명, 이미지 품질, 노이즈가 결과에 미치는 영향을 분석합니다.


|예측 결과| 의미| 실제 활용시 고려 할 점|
|--|--|--|
|사람의 눈 → 사람의 눈 (정확한 예측)| 모델이 올바르게 판별함| 신뢰할 수 있는 예측|
|사람이 아닌 물체 → 사람의 눈 (False Positive, FP)| 비정상적인 입력을 잘못 인식| 보안 취약점 발생 가능|
|사람의 눈 → 사람의 눈이 아님 (False Negative, FN)| 정상적인 눈을 부정확하게 예측| 인식률 저하, 사용자 불편|

# 모델 한계점 및 개선 가능성
1. Cataract Classifier 모델 한계점 및 개선 가능성
    - 경계가 모호한 백내장 초기 단계에 대한 분류 성능 저하 됩니다.
    - 백내장이 심한 경우는 비교적 쉽게 분류되지만, 초기 백내장은 정상과 유사하여 구별이 어려울 수 있습니다.
    - 데이터셋 내에서도 중증 백내장 데이터는 많지만, 초기 백내장 데이터는 상대적으로 적습니다.
    - Data Augmentation 및 Synthetic Data 활용 합니다.
    - 데이터셋의 불균형을 해결하기 위해, 이미지 증강(Augmentation) 기법을 적용하여 다양한 촬영 환경을 학습 하거나 GAN(Generative Adversarial Networks)이나 Diffusion 모델을 활용하여 초기 백내장 데이터를 인위적으로 생성합니다.

2. Eye Verification 모델 한계점 및 개선 가능성
    - False Positive 문제 (사람의 눈이 아닌데 눈으로 인식하는 경우)
    - False Negative 문제 (사람의 눈인데 눈이 아니라고 판단하는 경우)
    - Eye Verification 모델은 사전 크롭된 눈 영역을 입력으로 받기 때문에, 크롭이 부정확하면 모델의 성능도 저하됩니다.
    - 단순한 Eye Verification이 아니라, 이미 저장된 눈 임베딩과 비교하여 검증하는 방식으로 False Positive 감소
    - 크롭 방식 개선을 위해, RetinaFace 등 더 정밀한 얼굴 랜드마크 모델을 적용하여 정확한 눈 영역 추출

# 목표 성능 달성 여부
## 기대 성능과 비교
Cataract Classifier 모델의 목표 성능은 Accuracy 90% 이상, F1 Score 0.85 이상을 달성하는 것입니다.
또한, 특이도(Specificity) 90% 이상을 유지하는 것을 목표로 설정하였습니다.

|지표| 목표 성능| Test dataset 성능|
|--|--|--|
|Accuracy| 90% 이상| 0.9718|
|F1 Score| 85% 이상| 0.9636|
|Specificity| 95% 이상| 0.9808|

# 결론 및 향후 개선 방향
## 결론
본 모델은 백내장 분류(Cataract Classifier) 및 Eye Verification을 수행하기 위해 설계되었으며, MobileNet 기반의 경량화된 구조를 활용하여 온디바이스 환경에서도 원활한 실행이 가능하도록 최적화되었습니다.
평가 결과, 모델의 전반적인 성능은 목표 수준에 근접하거나 초과 달성하였으며, 특히 Eye Verification 모델은 높은 정확도를 기록하여 신뢰할 수 있는 검증 결과를 제공할 수 있음을 확인하였습니다.

하지만 몇가지의 한계점이 존재 합니다.
첫째로, 모델의 일반화 성능을 확보하기 위해 데이터의 다양성이 더 필요합니다.
둘째로, 특정 환경(조명, 안경 착용 등)에서 Eye Verification 모델의 False Positive 비율이 증가합니다.
## 향후 개선 방향
모델 성능 개선
- 데이터 증강(Augmentation) 기법 추가 적용 (특히 명암 변화 및 대비 조정)
- 의료 전문가의 추가 라벨링을 활용한 정밀 데이터셋 구축
