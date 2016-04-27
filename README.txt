
一、目錄結構

MaRe 遙控車運作所需的檔案都包含在本壓縮檔。

因為 Android 專案包含許多發佈程式碼不需要的部分，只保留部分的專案目錄結構。
要讓 Android App 可以編譯使用，請依照上課投影片，用 Android Studio 建立一個空
白的專案，並複製所需的內容。

   .
   ├── LICENCE.txt
   ├── MaRe (Android App Project)
   │   └── app
   │       └── src
   │           └── main
   │               ├── AndroidManifest.xml (資訊、權限設定)
   │               ├── java
   │               │   └── nctu
   │               │       └── ieilab
   │               │           └── ma_re
   │               │               └── MainActivity.java (主程式)
   │               └── res
   │                   └── layout
   │                       └── content_main.xml (使用者介面)
   ├── README.txt
   └── server.py (請複製到 Raspberry Pi 上)


二、使用教學

大家現在的 Raspberry Pi 是用有線的方式連線，但是自走車連著網路線跟電源線非常不
合理，所以請大家將 Raspberry Pi 改用 WiFi 連線並用自備的行動電源作為供電。
為避免馬達對 Raspberry Pi 瞬間抽取電流造成 Raspberry Pi 不正常運作，馬達另外透
過鋰電池供電。

   1. 設定 WiFi 連線

      以下教大家如何設定 WiFi 連線：

      a) 在電腦上用 PuTTY 或 MobaXterm 登入 Raspberry Pi
      b) 以系統管理員身分編輯下面2個檔案並存檔 (可以複製檔案內容開始到結尾處後並
         修改) /etc/network/interfaces、/etc/wpa_supplicant/wpa_supplicant.conf
   
         !!!注意!!!
         i)   請記得備份之前的有線網路設定
         ii)  連哪一台 WiFi AP 請詢問助教 (在家你可以自己決定)
         iii) 自己的 IP 是多少請詢問助教 (在家你可以自己決定)

            $ sudo nano /etc/network/interfaces

================================ 檔案內容開始 ================================
# interfaces(5) file used by ifup(8) and ifdown(8)

# Please note that this file is written to be used with dhcpcd
# For static IP, consult /etc/dhcpcd.conf and 'man dhcpcd.conf'

# Include files from /etc/network/interfaces.d:
source-directory /etc/network/interfaces.d

auto lo
iface lo inet loopback

iface eth0 inet manual

allow-hotplug wlan0
iface wlan0 inet static
address 192.168.1.32
netmask 255.255.255.0
gateway 192.168.1.1
dns-nameservers 8.8.8.8 8.8.4.4
    wpa-conf /etc/wpa_supplicant/wpa_supplicant.conf

allow-hotplug wlan1
iface wlan1 inet manual
    wpa-conf /etc/wpa_supplicant/wpa_supplicant.conf
================================ 檔案內容結束 ================================


            $ sudo nano /etc/wpa_supplicant/wpa_supplicant.conf

================================ 檔案內容開始 ================================
ctrl_interface=DIR=/var/run/wpa_supplicant GROUP=netdev
update_config=1

network={
    ssid="IEILab1"
    psk="ieilab01"
}
================================ 檔案內容結束 ================================

      c) 重新啟動 Raspberry Pi

            $ sudo reboot


   2. 啟動自走車連線伺服器
      a) 此時 Raspberry Pi 是 WiFi 連線
      b) 在電腦上用 PuTTY 或 MobaXterm 用助教指派的 IP 登入 Raspberry Pi
      c) 將 server.py 複製到自走車的 Raspberry Pi 家目錄
      d) 用下面指令將伺服器執行起來

           $ python ~/server.py 


   3. 用課程投影片編譯 Android App 並用手機執行
      a) 輸入助教指派的 IP
      b) 按下 Connect 按鈕
      c) 看訊息顯示 Connected!
         如果是「No route ...」的訊息且很肯定 IP 沒打錯
         試試多按幾次 Connect 按鈕
      d) 確定有畫面、可以操控：前進、後退
         可以看電腦上伺服器有沒有反應
         很多情況下是車子的程式寫錯或是硬體沒接好


   4. 開始改程式碼！
      加入以下功能：
      a) 左轉
      b) 右轉
      c) 氮氣 turbo
      d) 其他：例如你覺得很難操作可以修改介面


有其他問題，請聯絡 Gary Huang <gh.nctu+code@gmail.com>

