# What's that for? 

**Postgres Protocol Debugger** is a small command line utility that dumps all messages sent from a client to a postgres database.

a client is everything connecting to postgres, e.g. a gui tool, a java program using JDBC etc....

it will show you all the messages that are exchanged which makes debugging and analysing problems easier.

# Sample

start:

    $> pg-debugger.sh --server the_target_server --serverport 5432 --localport 6666
    $> psql --host localhost --port 6666 --user the_username my_database (in another window)


sample select executed against postgres via psql console:

    psql (9.0.1)
    Type "help" for help.

    my_database=>select 1;

output:

    waiting for incoming connections on port '6666' which will be forward to 'the_target_server:5432' ...
    established connection. starting to capture and forward messages ...

    [<-1>][<0><3><0><0>user<0>the_username<0>database<0>my_database<0>application<5f>name<0>psql<0><0>]
    [R][<0><0><0><0>]
    [S][application<5f>name<0>psql<0>]
    [S][client<5f>encoding<0>LATIN1<0>]
    [S][DateStyle<0>ISO<2c><20>MDY<0>]
    [S][integer<5f>datetimes<0>on<0>]
    [S][IntervalStyle<0>postgres<0>]
    [S][is<5f>superuser<0>off<0>]
    [S][server<5f>encoding<0>LATIN1<0>]
    [S][server<5f>version<0>9<2e>0<2e>1<0>]
    [S][session<5f>authorization<0>the_username<0>]
    [S][standard<5f>conforming<5f>strings<0>off<0>]
    [S][TimeZone<0>Europe<2f>Berlin<0>]
    [K][<0><0>j?Z<e>l<2e>]
    [Z][I]
    [Q][select<20>1<3b><0>]
    [T][<0><1><3f>column<3f><0><0><0><0><0><0><0><0><0><0><17><0><4><ffff><ffff><ffff><ffff><0><0>]
    [D][<0><1><0><0><0><1>1]
    [C][SELECT<20>1<0>]
    [Z][I]
    [X][]

# Getting Postgres Protocol Debugger

just download the latest zip file

or built from scratch

    $> git clone https://github.com/niesfisch/Postgres-Protocol-Debugger.git
    $> cd Postgres-Protocol-Debugger.git
    $> mvn clean package

# How To

1. start the postgres db server, remember which port is is now running on, e.g. 5432
2. start pg-debugger.sh

e.g.

    pg-debugger.sh --server homer --serverport 5432 --localport 7777

this will start a connection to host homer on port 5432 which is connected to your local port 7777

3. start any application that is using postgres and and change it to connect to localhost:7777

e.g.

   old: jdbc:postgresql://omega:5432/your_databsae
   new: jdbc:postgresql://localhost:7777/your_databsae

4. issue some SQL statements and watch the output generated

pg-debugger.sh <local port> <target port>

# Licence

Apache 2.0 http://www.apache.org/licenses/LICENSE-2.0.txt

# Release Notes

.... TBD

# Under the hood

the program acts as a proxy. it reads all the bytes send through the wire and tries to collect the messages.

the message parsing is based on the [Postgres Protocol V3.0](http://www.postgresql.org/docs/devel/static/protocol.html)

after parsing a message it is printed and the bytes are forwarded to postgres.
