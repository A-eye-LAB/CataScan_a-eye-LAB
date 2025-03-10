#!/bin/bash

download_path="dataset/data"

python dataset/download_datasets_1.py --download_path "$download_path"
python dataset/download_datasets_2.py --download_path "$download_path"
python dataset/find_duplicates.py --dataset_path "$download_path" --remove