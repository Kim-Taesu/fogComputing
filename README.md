## fogComputing



#### 목표

- 택시데이터를 카프카에 보낸뒤 카프카에서 시군구 고유 번호를 topic으로 설정하여 데이터 관리
- 포그 노드로 도커 컨테이너를 이용해 카프카로부터 각 포그가 관리하는 토픽을 가져오고 DB에 저장
- 클라우드에서 요청이 들어오면 알맞는 포그 노드에서 요청을 처리하고 클라우드에 결과 전송



#### 전체적인 구성

- client(device), fog server, kafka server, cloud server
  - client(device)
    - 데이터를 보내는 역할
  - kafka server
    - 데이터를 받고 토픽별로 분산 저장한다.
  - fog server
    - 도커를 사용하여 mongoDB 컨테이너를 띄운다.
    - kafka server로부터 데이터를 받고 원본데이터에 노이즈를 추가한다.
    - 원본데이터와, 노이즈 데이터를 각 행정구에 맞는 fognode의 mongoDB 컨테이너에 저장.
    - data homepage에서 각 행정구를 출력하면 getShowDetail,getShowDetailExpect class 를 이용하여 각 구의 시간별 택시 수를 확인한다.
  - cloud server
    - 시간 주기를 설정하여 fog server의 mongoDB 컨테이너의 데이터를 read하여 현재 현황을 json 파일로 저장한다.
    - 1번에서 저장한 json파일을 이용해 data homepage에 heatmap과 pie chart를 갱신한다.

  

#### 과정
1. client는 kafka server로 데이터를 전송한다.
2. fog server는 시간 주기별로 kafka server로부터 데이터를 가져오고 노이즈를 추가하여 각 fognode의 mongoDB에 저장한다.
3. cloud server는 시간 주기별로 fog node의 mongoDB의 데이터를 가져오고 데이터 현황을 json으로 저장한다.
4. 저장한 json 파일을 토대로 data homepage에 heatmap과 pie chart를 갱신한다.



#### 데이터
- 데이터는 서울 텍시데이터셋을 이용한다.
- url : http://data.seoul.go.kr/dataList/datasetView.do?infId=OA-12066&srvType=F&serviceKind=1



#### Technology

- mongoDB
- Apache Kafka



#### 개발언어

- Java
