
�@�B�ؿ����c

MaRe �������B�@�һݪ��ɮ׳��]�t�b�����Y�ɡC

�]�� Android �M�ץ]�t�\�h�o�G�{���X���ݭn�������A�u�O�d�������M�ץؿ����c�C
�n�� Android App �i�H�sĶ�ϥΡA�Ш̷ӤW�ҧ�v���A�� Android Studio �إߤ@�Ӫ�
�ժ��M�סA�ýƻs�һݪ����e�C

   .
   �u�w�w LICENCE.txt
   �u�w�w MaRe (Android App Project)
   �x   �|�w�w app
   �x       �|�w�w src
   �x           �|�w�w main
   �x               �u�w�w AndroidManifest.xml (��T�B�v���]�w)
   �x               �u�w�w java
   �x               �x   �|�w�w nctu
   �x               �x       �|�w�w ieilab
   �x               �x           �|�w�w ma_re
   �x               �x               �|�w�w MainActivity.java (�D�{��)
   �x               �|�w�w res
   �x                   �|�w�w layout
   �x                       �|�w�w content_main.xml (�ϥΪ̤���)
   �u�w�w README.txt
   �|�w�w server.py (�нƻs�� Raspberry Pi �W)


�G�B�ϥαо�

�j�a�{�b�� Raspberry Pi �O�Φ��u���覡�s�u�A���O�ۨ����s�ۺ����u��q���u�D�`��
�X�z�A�ҥH�Фj�a�N Raspberry Pi ��� WiFi �s�u�åΦ۳ƪ���ʹq���@���ѹq�C
���קK���F�� Raspberry Pi ��������q�y�y�� Raspberry Pi �����`�B�@�A���F�t�~�z
�L�Y�q���ѹq�C

   1. �]�w WiFi �s�u

      �H�U�Фj�a�p��]�w WiFi �s�u�G

      a) �b�q���W�� PuTTY �� MobaXterm �n�J Raspberry Pi
      b) �H�t�κ޲z�������s��U��2���ɮרæs�� (�i�H�ƻs�ɮפ��e�}�l�쵲���B���
         �ק�) /etc/network/interfaces�B/etc/wpa_supplicant/wpa_supplicant.conf
   
         !!!�`�N!!!
         i)   �аO�o�ƥ����e�����u�����]�w
         ii)  �s���@�x WiFi AP �и߰ݧU�� (�b�a�A�i�H�ۤv�M�w)
         iii) �ۤv�� IP �O�h�ֽи߰ݧU�� (�b�a�A�i�H�ۤv�M�w)

            $ sudo nano /etc/network/interfaces

================================ �ɮפ��e�}�l ================================
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
================================ �ɮפ��e���� ================================


            $ sudo nano /etc/wpa_supplicant/wpa_supplicant.conf

================================ �ɮפ��e�}�l ================================
ctrl_interface=DIR=/var/run/wpa_supplicant GROUP=netdev
update_config=1

network={
    ssid="IEILab1"
    psk="ieilab01"
}
================================ �ɮפ��e���� ================================

      c) ���s�Ұ� Raspberry Pi

            $ sudo reboot


   2. �Ұʦۨ����s�u���A��
      a) ���� Raspberry Pi �O WiFi �s�u
      b) �b�q���W�� PuTTY �� MobaXterm �ΧU�Ы����� IP �n�J Raspberry Pi
      c) �N server.py �ƻs��ۨ����� Raspberry Pi �a�ؿ�
      d) �ΤU�����O�N���A������_��

           $ python ~/server.py 


   3. �νҵ{��v���sĶ Android App �åΤ������
      a) ��J�U�Ы����� IP
      b) ���U Connect ���s
      c) �ݰT����� Connected!
         �p�G�O�uNo route ...�v���T���B�ܪ֩w IP �S����
         �ոզh���X�� Connect ���s
      d) �T�w���e���B�i�H�ޱ��G�e�i�B��h
         �i�H�ݹq���W���A�����S������
         �ܦh���p�U�O���l���{���g���άO�w��S���n


   4. �}�l��{���X�I
      �[�J�H�U�\��G
      a) ����
      b) �k��
      c) ��� turbo
      d) ��L�G�Ҧp�Aı�o�����ާ@�i�H�ק虜��


����L���D�A���p�� Gary Huang <gh.nctu+code@gmail.com>

