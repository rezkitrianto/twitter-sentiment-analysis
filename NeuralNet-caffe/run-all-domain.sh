topics=(apple google microsoft twitter)
percentage=(1 5 10 20)

mkdir allDomain/lmdb_data
for i in ${topics[@]}; do
	mkdir allDomain/lmdb_data/${i}
	for j in ${percentage[@]}; do		
		mkdir allDomain/lmdb_data/${i}/train_70_${j}_lmdb
		mkdir allDomain/lmdb_data/${i}/test_30_${j}_lmdb
		python convert.py allDomain/input/topic_${i}_data_training70.0_topic_${j}.0_percent.txt allDomain/lmdb_data/${i}/train_70_${j}_lmdb
		python convert.py allDomain/input/topic_${i}_data_testing30.0_topic_${j}.0_percent.txt allDomain/lmdb_data/${i}/test_30_${j}_lmdb
	done
done

mkdir allDomain/results
for i in ${topics[@]}; do
	for j in ${percentage[@]}; do
		caffe train --solver=allDomain/model/${i}${j}/nn_nlp_solver.prototxt 2>&1 | tee allDomain/results/${i}${j}.txt		
	done
done
