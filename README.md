### 테스트 방법은 첨부 파일 참조
### jar 파일 설정 및 mysql 계정에 해당하는 id와 password 입력해야 작동됩니다

---

**영화  예매  서비스  최종  보고서**

**[2024-1  데이터베이스  1반]**

- **기본사항**

|**프로젝트명**|온라인  영화  예약  시스템|||
| - | - | :- | :- |
|**작성자**||**작성일**|2024\.05.31|



- **데이터  베이스  설계**
1. **스키마  목록**



|**NO**|**테이블  ID**|**테이블  명**|
| - | - | - |
|1|movie|영화|
|2|schedule|상영일정|
|3|theater|상영관|
|4|ticket|티켓|
|5|seat|좌석|
|6|user|회원고객|
|7|Reservation|예매정보|

2. **스키마  정의**
1. 영화  (movie)



|**테이블  ID**|movie|**테이블  명**|영화|||||||||
| - | - | - | - | :- | :- | :- | :- | :- | :- | :- | :- |
|**테이블  설명**|상영중인  영화  정보를  저장한다.|||||||||||
|**NO**|**칼럼  명**|**칼럼  ID**|**타입**|**길이**|**PK**|**NN**|**UQ**|**UN**|**DEFAULT**|||
|1|영화번호|MovieNumber|INT||o|o|o|o||||
|2|영화명|MovieName|VARCHAR|25||o|o|||||
|3|상영시간|RunningTime|INT|||o||||||
|4|상영등급|FilmRatings|VARCHAR|20||||||||
|5|감독명|Director|VARCHAR|20||||||||
|6|배우명|Actor|VARCHAR|20||||||||
|7|장르|Genre|VARCHAR|20||||||||
|8|영화소개|MovieIntroduction|VARCHAR|100||||||||
|9|개봉일자|ReleaseDate|DATE|||o||||||
|10|평점|MovieRating|INT|||||o||||
2. 상영일정  (schedule)



|**테이블  ID**|상영일정|**테이블  명**|schedule||||||||||
| - | - | - | - | :- | :- | :- | :- | :- | :- | :- | :- | :- |
|**테이블  설명**|영화별  상영  일정  정보를  저장한다.||||||||||||
|**NO**|**칼럼  명**|**칼럼  ID**|**타입**|**길이**|**PK**|**NN**|**UQ**|**UN**|**DEFAULT**||||
|1|상영일정번호|ScheduleNumber|INT||o|o|o|o|||||
|2|영화번호|MovieNumber|INT|||o||o|||||
|3|상영관번호|TheaterNumber|INT|||o||o|||||
|4|상영시작일|ScreeningStartDate|DATE||||||||||
|5|상영요일|ScreeningDayOfWeek|VARCHAR|3||o|||||||
|6|상영회차|ScreeningRound|INT|||||o|||||
|7|상영시작시간|ScreeningStartTime|TIME|||o|||||||
3. 상영관  (theater)



|**테이블  ID**|상영관|**테이블  명**|theater||||||||||
| - | - | - | - | :- | :- | :- | :- | :- | :- | :- | :- | :- |
|**테이블  설명**|영화가  상영되는  상영관  정보를  저장한다.||||||||||||
|**NO**|**칼럼  명**|**칼럼  ID**|**타입**|**길이**|**PK**|**NN**|**UQ**|**UN**|**DEFAULT**||||
|1|상영관번호|TheaterNumber|INT||o|o||o|||||
|2|좌석수|NumberOfSeats|INT|||o||o|||||
|3|사용여부|TheaterAvailability|BOOL|||o|||||||
|4|가로좌석개수|HorizontalSeat|INT|||o||o|||||
|5|새로좌석개수|VerticalSeat|INT|||o|||||||
4. 티켓  (ticket)



|**테이블  ID**|티켓|**테이블  명**|ticket||||||||||
| - | - | - | - | :- | :- | :- | :- | :- | :- | :- | :- | :- |
|**테이블  설명**|회원이  예매한  영화의  티켓  정보를  저장한다.||||||||||||
|**NO**|**칼럼  명**|**칼럼  ID**|**타입**|**길이**|**PK**|**NN**|**UQ**|**UN**|**DEFAULT**||||
|1|티켓번호|TicketNumber|INT||o|o|o|o|||||
|2|상영일정번호|ScheduleNumber|INT|||o||o|||||
|3|상영관번호|TheaterNumber|INT|||o||o|||||
|4|좌석번호|SeatNumber|INT|||o||o|||||
|5|예매번호|ReservationNumber|INT|||o|o|o|||||
|6|발권여부|IssuanceStatus|BOOL||||||FALSE||||
|7|표준가격|StandardPrice|INT|||o||o|||||
|8|판매가격|SellingPrice|INT|||||o|||||
5. 좌석  (seat)



|**테이블  ID**|좌석|**테이블  명**|seat||||||||||
| - | - | - | - | :- | :- | :- | :- | :- | :- | :- | :- | :- |
|**테이블  설명**|상영관  좌석  별  정보를  저장한다.||||||||||||
|**NO**|**칼럼  명**|**칼럼  ID**|**타입**|**길이**|**PK**|**NN**|**UQ**|**UN**|**DEFAULT**||||
|1|좌석번호|SeatNumber|INT||o|o||o|||||
|2|상영관번호|TheaterNumber|INT|||o||o|||||
|3|좌석사용여부|SeatAvailability|BOOL|||o|||FALSE||||
|4|상영일정번호|ScheduleNumber|INT|||o||o|||||
- 상영일정  별  영화관  좌석번호를  부여하기위해,  각각을  구분하기  위한  상영일정번호가 새로 추가됨
6. 회원고객  (user)



|**테이블  ID**|회원고객|**테이블  명**|user||||||||||
| - | - | - | - | :- | :- | :- | :- | :- | :- | :- | :- | :- |
|**테이블  설명**|고객  별  개인  정보를  저장한다.||||||||||||
|**NO**|**칼럼  명**|**칼럼  ID**|**타입**|**길이**|**PK**|**NN**|**UQ**|**UN**|**DEFAULT**||||
|1|회원아이디|UserId|VARCHAR|20|o|o|o||||||
|2|고객명|UserName|VARCHAR|20||o|||||||
|3|휴대폰번호|PhoneNumber|CHAR|13||o|o||||||
|4|전자메일주소|Email|VARCHAR|30|||||||||
7. 예매정보  (reservation)



|**테이블  ID**|예매정보|**테이블  명**|reservation||||||||||
| - | - | - | - | :- | :- | :- | :- | :- | :- | :- | :- | :- |
|**테이블  설명**|고객의  영화예매  정보를  저장한다.||||||||||||
|**NO**|**칼럼  명**|**칼럼  ID**|**타입**|**길이**|**PK**|**NN**|**UQ**|**UN**|**DEFAULT**||||
|1|예매번호|ReservationNumber|INT||o|o|o||||||
|2|회원아이디|UserId|VARCHAR|20||o|||||||
|3|결제방법|PaymentMethod|VARCHAR|20||o|||||||
|4|결제상태|PaymentStatus|BOOL|||o|||FALSE||||
|5|결제금액|PaymentAmount|INT|||o||o|||||
|6|결제일자|PaymentDate|DATE||||||||||
|7|상영일정번호|ScheduleNumber|INT|||o|o|o|||||
3. **물리적  구상도**

![](Aspose.Words.04d23228-d8fd-43f0-a00c-9b9ddb380981.001.png)

4. **패키지  구조  설명**
1. **reservation패키지**
- 최상위  패키지로,  이  패키지  내에  customer와  manager서브패키지가  포함되어  있습니다.
- 또한,  Application.java와  LoginFrame.java클래스도  이  패키지  내에  존재합니다.
  2. **reservation.customer패키지**
     1. **BookingDetailFrame.java**:  예매된  영화의  상세  정보를  보여주는  프레임.
     1. **BookMovieFrame.java**:  영화를  예매하는  프레임.
     1. **CancelBookingFrame.java**:  예매를  취소하는  프레임.
     1. **ChangeBookedMovieFrame.java**:  예매된  영화를  다른  영화로  변경하는  프레임.
     1. **ChangeBookingScheduleFrame.java**:  예매된  영화의  상영  일정을  변경하는  프레임.
     1. **CustomerFrame.java**:  고객  메인  프레임,  로그인  후  보이는  첫  화면.
     1. **SelectSeatFrame.java**:  좌석  선택을  위한  프레임.
     1. **ViewBookingInfoFrame.java**:  예매  정보를  확인하는  프레임.
     1. **ViewMoviesFrame.java**:  영화  목록을  조회하고  예매하는  프레임.
  2. **reservation.manager패키지**
     1. **DatabaseInitDialog.java**:  데이터베이스  초기화를  담당하며  성공  여부를  출력하는  창
     1. **DatabaseModifyFrame.java**:  데이터베이스를  수정하는  프레임.
     1. **ManagerFrame.java**:  관리자  메인  프레임.
     1. **ShowTablesFrame.java**:  데이터베이스  테이블을  보여주는  프레임.
  2. **루트  패키지  (reservation)**
     1. **Application.java**:  애플리케이션의  메인  클래스.  프로그램  실행의  시작점.
     1. **LoginFrame.java**:  로그인  프레임.  고객과  관리자가  로그인을  수행하는  화면.
5. **서비스  설명**
1. **로그인  과정**:
- LoginFrame.java를  통해  고객  또는  관리자가  로그인합니다

  (관리자  root/1234  사용자  user1/user1  계정으로  로그인  진행)

- 로그인  성공  시,  고객은  CustomerFrame.java로,  관리자는  ManagerFrame.java로  이동합니다.
2. **고객  관련  기능**:
   1. CustomerFrame.java는  고객을  위한  메인  화면입니다.
   1. 고객은  ViewMoviesFrame.java를  통해  영화를  조회하고  예매할  수  있습니다.
   1. 예매를  완료하면,  ViewBookingInfoFrame.java에서  자신의  예매  정보를  확인할  수  있습니다.
   1. 필요시  CancelBookingFrame.java에서  예매를  취소할  수  있습니다.
   1. ChangeBookedMovieFrame.java와  ChangeBookingScheduleFrame.java를  통해  예매한  영화나 상영  일정을  변경할  수  있습니다.
   1. SelectSeatFrame.java를  통해  좌석을  선택할  수  있습니다.
   1. BookingDetailFrame.java를  통해  예매된  영화의  상세  정보를  확인할  수  있습니다.
2. **관리자  관련  기능**:
- ManagerFrame.java는  관리자를  위한  메인  화면입니다.
- 관리자는  DatabaseInitDialog.java를  통해  데이터베이스  초기화  작업을  수행할  수  있습니다.
- DatabaseModifyFrame.java를  통해  데이터베이스를  수정할  수  있습니다.
- ShowTablesFrame.java를  통해  데이터베이스  테이블을  조회할  수  있습니다.

**5. 테스팅  방법**

**1.  관리자**

![](Aspose.Words.04d23228-d8fd-43f0-a00c-9b9ddb380981.002.jpeg)

1. 관리자  체크  및  관리자  계정  (root/1234)  입력  후  로그인  (ManagerFrame  으로  이동)

   ![](Aspose.Words.04d23228-d8fd-43f0-a00c-9b9ddb380981.003.jpeg)

2. 초기화,  수정,  전체  테이블  보기  및  로그아웃  기능  사용  가능

![](Aspose.Words.04d23228-d8fd-43f0-a00c-9b9ddb380981.004.jpeg)

3. 데이터베이스  초기화  버튼을  클릭하여  초기화  진행

![](Aspose.Words.04d23228-d8fd-43f0-a00c-9b9ddb380981.005.jpeg)![](Aspose.Words.04d23228-d8fd-43f0-a00c-9b9ddb380981.006.jpeg)

4. 데이터  베이스  수정은  SQL  쿼리문  형태로  입력하여  데이터베이스에  대한  수정이  가능함

(ex.

INSERT  INTO movie  (MovieNumber,  MovieName,  RunningTime,  FilmRatings,  Director,  Actor,  Genre, MovieIntroduction,  ReleaseDate,  MovieRating)  VALUES (13,  '기생충',  132,  '15세  관람가',  '봉준호',  '송강호',  '스릴러', '가난한  가족이  부잣집에  기생하면서  벌어지는  이야기.',  '2024-05-30',  9);

)

![](Aspose.Words.04d23228-d8fd-43f0-a00c-9b9ddb380981.007.jpeg)

4. 전체  테이블  보기  기능을  통해  테이블  별  삽입되어있는  데이터를  확인할  수  있음

   5. 로그아웃  버튼을  통해  로그인  페이지로  되돌아가도록  구현

**1.  사용자**

![](Aspose.Words.04d23228-d8fd-43f0-a00c-9b9ddb380981.008.jpeg)

1. 사용자  체크  및  사용자  계정  (user1/user1)  입력,  로그인하고자  하는  사용자ID를

combobox에서  선택  하여  로그인  (DB초기화  시  12개의  사용자  생성  /  CustomerFrame으로  이동)

![](Aspose.Words.04d23228-d8fd-43f0-a00c-9b9ddb380981.009.jpeg)

2. 로그인  시  영화  조회  및  예매,  예매정보  확인,  로그아웃  기능  사용  가능 로그아웃  선택  시  로그인  페이지로  되돌아가도록  구현

![](Aspose.Words.04d23228-d8fd-43f0-a00c-9b9ddb380981.010.jpeg)![](Aspose.Words.04d23228-d8fd-43f0-a00c-9b9ddb380981.011.jpeg)

3. 영화  조회  및  예매  클릭  시  현재  상영중인  모든  영화에  대한  상영일정이  조회됨
4. 상단의  ComboBox를  통해  영화명,  감독명,  배우명,  장르를  이용한  조회가  가능해짐 (입력  안된  정보는  무시하고  조회)

![](Aspose.Words.04d23228-d8fd-43f0-a00c-9b9ddb380981.012.jpeg)

5. 원하는  값을  선택  후  조회버튼을  누르면  해당하는  상영  내역만  표시됨

![](Aspose.Words.04d23228-d8fd-43f0-a00c-9b9ddb380981.013.jpeg)![](Aspose.Words.04d23228-d8fd-43f0-a00c-9b9ddb380981.014.jpeg)

6. 예매하고자  하는  상영일정을  선택  후  예매  버튼을  누르면

예매하고자  하는  영화의  상제  정보와  함께  GUI로  구현된  좌석  선택  페이지가  나타남

7. 본인  혹은  다른  사용자로부터  이미  예매가  되어있는  좌석에  대해서는  선택하지  못하도록  구현됨

![](Aspose.Words.04d23228-d8fd-43f0-a00c-9b9ddb380981.015.jpeg)

8. 예매하고자  하는  좌석  선택  시  노란색으로  변경,  예매  완료  버튼을  누르면  예매  성공  메시지  출력됨

![](Aspose.Words.04d23228-d8fd-43f0-a00c-9b9ddb380981.016.jpeg)![](Aspose.Words.04d23228-d8fd-43f0-a00c-9b9ddb380981.017.jpeg)

9. 예매정보  확인  버튼  클릭  시  현재  예매되어있는  영화  내역이  나타남

![](Aspose.Words.04d23228-d8fd-43f0-a00c-9b9ddb380981.018.jpeg)

10. 예매내역을  더블클릭하면  해당  내역에  대한  상세정보  페이지가  나타남 상단에서부터  티켓정보,  예매  상세  정보,  영화  정보  순으로  출력됨

![](Aspose.Words.04d23228-d8fd-43f0-a00c-9b9ddb380981.019.jpeg)![](Aspose.Words.04d23228-d8fd-43f0-a00c-9b9ddb380981.020.jpeg)

11. 예매  취소는  예매  번호를  통한  취소  기능이며,  여러개의  예매  취소는  쉼표로  구분하여  취소  가능
- 로그인한  사용자의  예매번호가  아니거나  존재하지  않는  번호에  대한  취소는  불가능하도록  구현

  ![](Aspose.Words.04d23228-d8fd-43f0-a00c-9b9ddb380981.021.jpeg)

12. 예매  영화  변경의  경우,  현재  예매되어있는  내역을  선택하고  버튼을  눌러  실행  가능

변경하고자  하는  다른  영화의  상영일정을  선택  후  버튼을  눌러  좌석  선택  페이지로  이동

![](Aspose.Words.04d23228-d8fd-43f0-a00c-9b9ddb380981.022.png)![](Aspose.Words.04d23228-d8fd-43f0-a00c-9b9ddb380981.023.png)

13. 예매가  불가능한  좌석은  빨간색으로  구현,  원하는  좌석  선택  시  예매  완료  메시지  출력

    ![](Aspose.Words.04d23228-d8fd-43f0-a00c-9b9ddb380981.024.jpeg)

14. 예매  날짜  변경의  경우,  현재  예매되어있는  내역을  선택하고  버튼을  눌러  실행  가능 변경하고자  하는  상영  일정을  선택  후  버튼을  눌러  좌석  선택  페이지로  이동

![](Aspose.Words.04d23228-d8fd-43f0-a00c-9b9ddb380981.025.png)![](Aspose.Words.04d23228-d8fd-43f0-a00c-9b9ddb380981.026.png)

15. 예매가  불가능한  좌석은  빨간색으로  구현,  원하는  좌석  선택  시  예매  완료  메시지  출력
