kill $(ps -ef | pgrep -f "stocks-price-scheduler")
nohup java -Dspring.profiles.active=production -jar stocks-price-scheduler-0.0.1-SNAPSHOT.jar 1> /dev/null 2>&1 &
tail -f /home/ec2-user/scheduler/logs/*
