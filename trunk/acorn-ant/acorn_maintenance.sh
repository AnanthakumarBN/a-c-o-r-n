#! /bin/sh
gf_dir='/usr/local/glassfishv3/glassfish'
kill -9 `ps -ef | grep localhost:3306 | grep -v grep | awk '{print $2}'`
$gf_dir/bin/asadmin stop-domain domain1
./backup_bazy.sh
$gf_dir/bin/asadmin start-domain domain1
./run_acorn_worker.sh 1 &
./run_acorn_worker.sh 2 &
./run_acorn_worker.sh 3 &
