#read -p "Enter Text: " text
text=${text:-Folder $1 >> $2.png}
echo $text

adb shell screencap -p /sdcard/screen.png

#adb shell am broadcast -a ScreenActivity --es locale "0,0,'$text'"


adb pull /sdcard/screen.png "$1/$2.png"
adb shell rm /sdcard/screen.png

#convert  -resize 50% "$1/$2.png" "$1/$2-50p.png"
convert -resize 21% "$1/$2.png" "$1/$2.png"