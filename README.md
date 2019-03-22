# fogKafka

1. kafka consumer로 kafka 서버에서 지정된 토픽의 갱신된 내용을 가져온다.
2. 1번의 가져온 데이터를 각 fog 노드의 hbase로 저장한다.
3. fog노드에서 저장된 데이터를 확인하고 cloud 서버로 데이터를 보낸다.

2-1. fog노드는 도커 컨테이너로 실행한다. (hbase, java)
3-1. fog-cloud 데이터 통신은 멀티 쓰레드 통신으로 한다.

### 데이터
-------------------------------
데이터는 서울 텍시데이터셋을 이용한다.
url : http://data.seoul.go.kr/dataList/datasetView.do?infId=OA-12066&srvType=F&serviceKind=1
