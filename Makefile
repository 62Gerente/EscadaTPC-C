export projdir=$(shell pwd)/..
include ./Makefile.vars
GARBAGE=`find . -name \*.class`

export SRCS=$(shell find escada/tpc -name \*.java)

all: jar

compile: $(SRCS)
	$(JC) $(JIKES_FLAGS) -g $^
	
jar: compile
	(cd $(classesdir) ; $(JAR) $(JAR_FLAGS) $(PROJ_JAR) `find  escada/tpc -name \*.class`);

clean:
	rm -rf $(GARBAGE)
	echo $(GARBAGE)
	
ORACLE_FLAGS=-EBclass escada.tpc.tpcc.TPCCEmulation \
             -KEY false \
             -CLI 50 \
             -STclass escada.tpc.tpcc.TPCCStateTransition \
             -DBclass escada.tpc.tpcc.database.oracle.dbOracle \
             -TRACEflaf TRACE \
             -PREFIX Client \
             -DBpath jdbc:oracle:thin:@192.168.2.32:1521:tpccdb \
             -DBdriver oracle.jdbc.driver.OracleDriver \
             -DBusr tpcc \
             -DBpasswd tpcc \
             -POOL 50 \
             -MI 45 \
	     -FRAG 1

MYSQL_FLAGS=-EBclass escada.tpc.tpcc.TPCCEmulation \
             -KEY false \
             -CLI 50 \
             -STclass escada.tpc.tpcc.TPCCStateTransition \
             -DBclass escada.tpc.tpcc.database.mysql.dbMySql \
             -TRACEflag TRACE \
             -PREFIX Client \
             -DBpath jdbc:mysql://192.168.2.144/tpcc \
             -DBdriver com.mysql.jdbc.Driver \
             -DBusr root \
             -DBpasswd 123456 \
             -POOL 50 \
             -MI 45 \
	     -FRAG 1

PGSQL_FLAGS=-EBclass escada.tpc.tpcc.TPCCEmulation \
             -KEY false \
             -CLI 1 \
             -STclass escada.tpc.tpcc.TPCCStateTransition \
             -DBclass escada.tpc.tpcc.database.postgresql.dbPostgresql \
             -TRACEFLAG TRACE \
             -PREFIX TPC-C \
             -DBpath jdbc:postgresql://localhost:5432/tpcc \
             -DBdriver org.postgresql.Driver \
             -DBusr tpcc \
             -DBpasswd tpcc \
             -POOL 1 \
             -MI 45 \
	     -FRAG 1

real-oracle:
	$(JVM) -cp $(classpath) -Xmx1024 escada.tpc.common.clients.ClientStartup $(ORACLE_FLAGS)

real-mysql:
	$(JVM) -cp $(classpath) -Xmx1024 escada.tpc.common.clients.ClientStartup $(MYSQL_FLAGS)

real-pgsql:
	$(JVM) -cp $(classpath) -Xmx1024 escada.tpc.common.clients.ClientStartup $(PGSQL_FLAGS)


# arch-tag: 35a104c6-523c-493b-9afe-e85f72d9d865
