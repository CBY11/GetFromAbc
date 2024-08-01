@echo off
rem set source=F:\Develop\DevEcoStudio_File\funcTest8\entry\build\default\outputs\default\entry-default-unsigned.hap
set source=F:\Develop\DevEcoStudio_File\hds\HealthyLife\entry\build\default\outputs\default\entry-default-unsigned.hap
set destination=F:\Develop\Java_code\Huawei\GetFromAbc\batTest
set extract_folder=F:\Develop\Java_code\Huawei\GetFromAbc\batTest\

rem 删除路径下的所有文件和文件夹
rmdir /s /q "%destination%"

rem 重新创建目标路径
mkdir "%destination%"

rem 复制文件到目标位置
copy /y "%source%" "%destination%\"

rem 将文件后缀名改为.zip
ren "%destination%\entry-default-unsigned.hap" "hap2zip.zip"

"C:\Program Files\WinRAR\WinRAR.exe" x %extract_folder%\hap2zip.zip %extract_folder%

rmdir /s /q "%destination%"\resources
del "%destination%"\*.json
del "%destination%"\*.index
del "%destination%"\*.info
copy /y "%extract_folder%"ets\modules.abc "%extract_folder%"
rmdir /s /q %extract_folder%ets
ark_disasm %extract_folder%modules.abc %extract_folder%out.txt
ark_disasm --verbose %extract_folder%modules.abc %extract_folder%out2.txt

echo 操作完成
pause
