# Foodie
## 관련 문서
* [Test Case 종류](https://github.com/hanseongbugi/Foodie/blob/main/doc/TestCase_FOODIE.pdf)
* [UI 설계](https://github.com/hanseongbugi/Foodie/blob/main/doc/UI_FOODIE.pdf)
* [요구사항 분석](https://github.com/hanseongbugi/Foodie/blob/main/doc/%EC%9A%94%EA%B5%AC%EC%82%AC%ED%95%AD%EB%B6%84%EC%84%9D%EC%84%9C_FOODIE.pdf)
## 사용법
Wear 폴더를 android studio로 열면 와치용 앱이 열립니다.

회원 가입시 비밀번호 형식은 영어+숫자 9~20자리 입니다.

테스트시 잘 안된다면 testcase를 참고해 주세요 

## 제약사항
1. 게시물 포스팅(PR1-3)에 사진, 글, 위치, 리뷰를 모두 적어야 한다.
2. 회원가입(PR1-1)시, 이메일 형식을 지켜야한다.
3. 회원가입(PR1-1)시, 비밀번호는 영어와 숫자를 섞어 9-20자로 만들어야한다.
4. 와이파이, 데이터 등 인터넷이 가능한 안드로이드 모바일 디바이스에서 실행하여야 한다.
5. 화면 전환 시, 이미지가 전부 로딩된 이후에 진행한다.
6. 웨어러블 갤럭시 앱을 설치한 후 WearOS와 연결해야한다.
7. 게시물 업로드시 영상을 제외한 사진만 허용한다.
8. 평점은 0~5 사이 실수로 제한한다.
9. Firebase Storage 할당량 내에서 서비스를 이용하여야 한다.
10. 게시물 추가시 이름과 장소는 search버튼을 통해서 데이터를 추가한다.
