#! /bin/sh

db_user="acorn"
db_pass="acorn"

db_driver="com.mysql.jdbc.jdbc2.optional.MysqlDataSource"
db_url="jdbc:mysql://localhost:3306/acorn-db"
model_dump="/home/acorn/model_dump"
datka=`date +%Y%m%d_%H%M`
iiop_port="3700"

logFile="/usr/local/acorn/acorn-worker/logs/worker_log_"$datka_$1
amkfbaPath="/usr/local/acorn/amkfba/amkfba"
jarfile="/usr/local/acorn/acorn-worker/acorn-worker.jar"
path_to_jdk="/usr/java/jdk1.6.0_10"

$path_to_jdk/bin/java -jar -Dtoplink.jdbc.user=$db_user -Dtoplink.jdbc.password=$db_pass -Dtoplink.jdbc.driver=$db_driver -Dtoplink.jdbc.url=$db_url -Dacorn.worker.logfile="$logFile" -Dacorn.worker.amkfbaPath="$amkfbaPath" -Dorg.omg.CORBA.ORBInitialPort=$iiop_port -Dacorn.worker.printInput=true -Dacorn.worker.modelDump="$model_dump" $jarfile
