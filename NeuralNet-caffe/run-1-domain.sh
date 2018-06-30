#!/usr/bin/env bash
topics=(APPLE GOOGLE MICROSOFT TWITTER)
mkdir 1Domain/lmdb_data
for i in ${topics[@]}; do
	mkdir 1Domain/lmdb_data/${i}
	mkdir 1Domain/lmdb_data/${i}/train_70_lmdb
	mkdir 1Domain/lmdb_data/${i}/test_30_lmdb
	python convert.py 1Domain/input/${i}_data_training70.0.txt 1Domain/lmdb_data/${i}/train_70_lmdb
	python convert.py 1Domain/input/${i}_data_testing30.0.txt 1Domain/lmdb_data/${i}/test_30_lmdb
done

mkdir 1Domain/results
for i in ${topics[@]}; do
	caffe train --solver=1Domain/model/${i}/nn_nlp_solver.prototxt 2>&1 | tee 1Domain/results/${i}.txt		
done
