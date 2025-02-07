
**온라인  영화  예약  시스템**
**[2024-1  데이터베이스]**
|**작성일**|2024\.05.31|
---
## **테스트 방법**
**첨부 파일 참조**
#### jar 파일 설정 및 mysql 실행 후 계정에 해당하는 id와 password 입력해야 작동됩니다
다음 이미지를 참고하여 테스트를 진행하세요.

![테스트 방법 1](img/테스트%20방법_page-0001.jpg)  
![테스트 방법 2](img/테스트%20방법_page-0002.jpg)  
![테스트 방법 3](img/테스트%20방법_page-0003.jpg)  
![테스트 방법 4](img/테스트%20방법_page-0004.jpg)  
![테스트 방법 5](img/테스트%20방법_page-0005.jpg)  
![테스트 방법 6](img/테스트%20방법_page-0006.jpg)  
![테스트 방법 7](img/테스트%20방법_page-0007.jpg)  
![테스트 방법 8](img/테스트%20방법_page-0008.jpg)  
![테스트 방법 9](img/테스트%20방법_page-0009.jpg)


#### 패키지  구조

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
