#!/bin/bash
datka=`date +%Y%m%d_%H%M`
echo $datka
where='/home/acorn/backup_mysql'
root_user='root'
root_pass='root'
mysqldump -u $root_user --databases acorn-db > $where/mysqlbackup_acorn_$datka.sql --password=$root_pass
mysqldump -u $root_user --all-databases > $where/mysqlbackup_all_$datka.sql --password=$root_pass
