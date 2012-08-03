# What's that for? 

**Traffic Dumper** is a small command line utility that dumps the traffic sent between two applications.

it acts like a proxy dumping all bytes sent through 'the wire'.

# Sample connecting to a PostgreSQL database

start:

    $> traffic-dumper.sh --targetserver the_target_server --targetport 5432 --localport 7777 > out.log
    $> psql --host localhost --port 7777 --user the_username my_database (in another window)

sample select executed against postgres via psql console:

    psql (9.0.1)
    Type "help" for help.

    my_database=>select 1;

output:

    waiting for incoming connections on port '7777' which will be forward to 'xxx:5432' ...
    established connection. starting to capture and forward messages ...

    [SERVER] >, bytes [<0x4e>]
    [SERVER] >, data  [.....]
    [CLIENT] >, bytes [<0x0><0x0><0x0><0x8><0x4><0xffffffd2><0x16><0x2f>]
    [CLIENT] >, data  [.....]
    [CLIENT] >, bytes [<0x0><0x0><0x0><0x42>...<0x6c><0x0><0x0>]
    [CLIENT] >, data  [.....  ]
    [SERVER] >, bytes [<0x52>...<0x49>]
    [SERVER] >, data  [.....]

# Getting Traffic Dumper

just download the latest zip file

or built from scratch

    $> git clone https://github.com/niesfisch/traffic-dumper.git
    $> cd traffic-dumper.git
    $> mvn clean package

# How To

    traffic-dumper.sh --targetserver homer --targetport 1234 --localport 7777 > out.log

this will start a connection to host 'homer' on port '1234' which is connected to your local port '7777'

now start your application and connect it to localhost:7777

e.g. for postgres

    old: jdbc:postgresql://homer:5432/your_databsae
    new: jdbc:postgresql://localhost:7777/your_databsae

issue some SQL statements and watch the output generated

# Licence

Apache 2.0 http://www.apache.org/licenses/LICENSE-2.0.txt

# Release Notes

.... TBD

# Under the hood

the program acts as a proxy. it reads all the bytes send through the wire and tries to collect the messages.

the message parsing is based on the [Postgres Protocol V3.0](http://www.postgresql.org/docs/devel/static/protocol.html)

after parsing a message it is printed and the bytes are forwarded to postgres.
