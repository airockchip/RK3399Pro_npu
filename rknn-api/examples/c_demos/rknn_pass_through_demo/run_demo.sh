export LD_LIBRARY_PATH=./lib/

case $1 in
	uint8)
	./rknn_pass_through_demo  model/mobilenet_v2_UINT8.rknn  model/dog_224x224.jpg
	;;
	int8)
	./rknn_pass_through_demo  model/mobilenet_v2_INT8.rknn  model/dog_224x224.jpg
	;;
	int16)
	./rknn_pass_through_demo  model/mobilenet_v2_INT16.rknn  model/dog_224x224.jpg
	;;
	fp)
	./rknn_pass_through_demo  model/mobilenet_v2_FP.rknn model/dog_224x224.jpg
	;;
	*)
	echo './run.sh [uint8 | int8 | int16 | fp]'
	;;
esac


