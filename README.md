# JOIN ME
## 目錄
   1. [簡介](README.md#簡介)
   2. [架構](README.md#架構)
   3. [技術](README.md#技術)
   4. [企劃書](README.md#企劃書)
## 簡介
      
      　　此為一款線上即時揪團 APP，目的是為了解決用戶臨時找不到人一起參加活動的問題，且市面上現有的 APP 舉辦活動的手續較為繁
      瑣，因此推出此款 APP 讓用戶利用制式化的表單填寫，即能在短時間內完成舉辦活動，較有利於中小型活動的舉辦。此外，透過O2O商業
      模式，讓使用者在線上平台揪團的同時也能在線下交友。
          透過 google map 呈現活動舉辦所在處，使用戶能更直觀的瀏覽。在 APP 內還有聊天室的功能，用戶在成功建立一個活動後，
      能夠透過聊天室和其他參與者進行溝通互動。此外，為了預防用戶因個人行為，如：爽約、未事先預繳活動費用，而造成他人權益受損
      的問題， APP 內還設置了評價機制，讓用戶們能夠在審核是否讓該使用者參與活動時，有個參考的依據。        
      
![image](https://github.com/ziyen0807/joinme--/blob/main/%E5%9C%96%E7%89%871.png)     
## 架構
| 主要頁面        | 主要功能           |
| ------------- |-------------|
|     首頁     |  地圖顯示活動  |
|    個人頁面   | 好友列表<br>參與活動的歷史紀錄<br>活動評論
|    報名頁面   | 報名資料
揪團頁面        | 主辦方填寫活動詳細資訊
通知頁面        | 評論回覆<br>審核通知<br>交友邀請
審核頁面        | 審核活動參與人
聊天室頁面      | 聊天記錄<br>群組列表<br>好友列表
## 技術
   * DB：透過 firebase 作為連結整個 APP 的資料庫。
   * API：運用 google map API 和 google place API 作為 APP 主要頁面呈現， Place API 則讓用戶更精確的標記活動地點。
   * Programing Language：Java。
## 企劃書
   
