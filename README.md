# fogKafka

## 전체적인 구성
--------------------------------
client(device), fog server, kafka server, cloud server

###### client(device)
1. 데이터를 보내는 역할

###### kafka server
1. 데이터를 받고 토픽별로 분산 저장한다.

###### fog server
1. 도커를 사용하여 mongoDB 컨테이너를 띄운다.
2. kafka server로부터 데이터를 받고 원본데이터에 노이즈를 추가한다.
3. 원본데이터와, 노이즈 데이터를 각 행정구에 맞는 fognode의 mongoDB 컨테이너에 저장.
4. data homepage에서 각 행정구를 출력하면 getShowDetail,getShowDetailExpect class 를 이용하여 각 구의 시간별 택시 수를 확인한다.

###### cloud server
1. 시간 주기를 설정하여 fog server의 mongoDB 컨테이너의 데이터를 read하여 현재 현황을 json 파일로 저장한다.
2. 1번에서 저장한 json파일을 이용해 data homepage에 heatmap과 pie chart를 갱신한다.

### 과정
-------------------------------
1. client는 kafka server로 데이터를 전송한다.
2. fog server는 시간 주기별로 kafka server로부터 데이터를 가져오고 노이즈를 추가하여 각 fognode의 mongoDB에 저장한다.
3. cloud server는 시간 주기별로 fog node의 mongoDB의 데이터를 가져오고 데이터 현황을 json으로 저장한다.
4. 저장한 json 파일을 토대로 data homepage에 heatmap과 pie chart를 갱신한다.

### 데이터
-------------------------------
데이터는 서울 텍시데이터셋을 이용한다.
url : http://data.seoul.go.kr/dataList/datasetView.do?infId=OA-12066&srvType=F&serviceKind=1
