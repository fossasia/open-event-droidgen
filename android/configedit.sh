VAR=0
STRING1=$1
while read line
do
((VAR+=1))
if [ "$VAR" = 4 ]; then
echo "$STRING1"
else
echo "$line"
fi
done < app/src/main/assets/config.json