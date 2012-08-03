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
    [SERVER] >, data  [N]
    [CLIENT] >, bytes [<0x0>....<0x2f>]
    [CLIENT] >, data  [<0x0><0x0><0x0><0x8><0x4>?<0x16>/]
    ...
    [CLIENT] >, data  [Q<0x0><0x0><0x0><0xe>select 1;<0x0>]
    [SERVER] >, bytes [...]
    [CLIENT] >, data  [X<0x0><0x0><0x0><0x4>]

# Sample connecting to www.google.com

start:

    $> traffic-dumper.sh --targetserver www.google.com --targetport 80 --localport 6666 > out.log
    $> telnet localhost 6666

now we use telnet to query against localhost

    telnet: Trying ...
    Connected to localhost.
    Escape character is '^]'.
    GET / HTTP/1.1
    Host: www.google.com
    \n
    \n

output:

    waiting for incoming connections on port '6666' which will be forward to 'www.google.com:80' ...
    established connection. starting to capture and forward messages ...

    [CLIENT] >, bytes [<0x47><0x45><0x54><0x20><0x2f><0x20><0x48><0x54><0x54><0x50><0x2f><0x31><0x2e><0x31><0xd><0xa>]
    [CLIENT] >, data  [GET / HTTP/1.1<0xd><0xa>]
    [CLIENT] >, bytes [<0x48><0x6f><0x73><0x74><0x3a><0x20><0x77><0x77><0x77><0x2e><0x67><0x6f><0x6f><0x67><0x6c><0x65><0x2e><0x63><0x6f><0x6d><0xd><0xa>]
    [CLIENT] >, data  [Host: www.google.com<0xd><0xa>]
    [CLIENT] >, bytes [<0xd><0xa>]
    [CLIENT] >, data  [<0xd><0xa>]
    [SERVER] >, bytes [<0x68><0x74><0x74><0x70><0x3a><0x2f><0x2f><0x77><0x77><0x77><0x2e><0x67>..]
    [SERVER] >, data  [http://www.google.de/">here</A>.<0xd><0xa></BODY></HTML><0xd><0xa>]
    [SERVER] >, bytes [<0x48><0x54><0x54><0x50><0x2f><0x31><0x2e><0x31><0x20><0x33><0x30><0x32>...]
    [SERVER] >, data  [HTTP/1.1 302 Found<0xd><0xa>Location: http://www.google.de/<0xd><0xa>...]
    [SERVER] >, bytes [<0x30><0x20><0x47><0x4d><0x54><0x3b><0x20><0x70><0x61><0x74><0x68><0x3d>...]
    [SERVER] >, data  [0 GMT; path=/; domain=.google.com<0xd><0xa>Set-Cookie: path=; expires=Mon,...]
    [SERVER] >, bytes [<0x63><0x6f><0x6d><0xd><0xa><0x53><0x65><0x74><0x2d><0x43><0x6f><0x6f><0x6b...]
    [SERVER] >, data  [com<0xd><0xa>Set-Cookie: path=; expires=Mon, 01-Jan-1990 00:00:00 GMT; path=/;...]


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