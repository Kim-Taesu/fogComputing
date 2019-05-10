# fogKafka

## 전체적인 구성
--------------------------------
client(device), fog server, kafka server, cloud server

###### client(device)
1. 데이터를 보내는 역할

###### kafka server
1. 데이터를 받고 토픽별로 분산 저장한다.

###### fog server
1. 도커를 사용하여 hbase with java 컨테이너를 띄운다.
2. kafka server로부터 데이터를 받고 각 fog hbase 컨테이너에 저장.
3. cloud server로 받은 데이터 전송
4. getShowDetail class 를 이용하여 각 구의 시간별 택시 수를 확인한다.

###### cloud server
1. 데이터를 받으면 파이썬을 통해 서울 택시 통계를 지도 파일로 출력한다. (svg file)
2. 웹 서버로 


### 과정
-------------------------------
1. kafka consumer로 kafka 서버에서 지정된 토픽의 갱신된 내용을 가져온다.
2. 1번의 가져온 데이터를 각 fog 노드의 hbase로 저장한다.
3. fog노드에서 저장된 데이터를 확인하고 cloud 서버로 데이터를 보낸다.

### 참고사항
-------------------------------
2-1. fog노드는 도커 컨테이너로 실행한다. (hbase, java)
3-1. fog-cloud 데이터 통신은 멀티 쓰레드 통신으로 한다.

### 데이터
-------------------------------
데이터는 서울 텍시데이터셋을 이용한다.
url : http://data.seoul.go.kr/dataList/datasetView.do?infId=OA-12066&srvType=F&serviceKind=1
