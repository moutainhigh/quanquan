修改hbase的'CIRCLE.ANSLOG' 表，添加rules 列簇，否则会报错
alter 'CIRCLE.ANSLOG',NAME=>'rules',VERSIONS=>5