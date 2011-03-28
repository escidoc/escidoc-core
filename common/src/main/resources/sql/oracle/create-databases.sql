create database escidoc
  logfile   group 1 ('/usr/lib/oracle/xe/oradata/escidoc-core/redo1.log') size 10M,
            group 2 ('/usr/lib/oracle/xe/oradata/escidoc-core/redo2.log') size 10M,
            group 3 ('/usr/lib/oracle/xe/oradata/escidoc-core/redo3.log') size 10M
  character set          utf8
  national character set utf8
  datafile '/usr/lib/oracle/xe/oradata/escidoc-core/system.dbf' 
            size 50M
            autoextend on 
            next 10M maxsize unlimited
            extent management local
  sysaux datafile '/usr/lib/oracle/xe/oradata/escidoc-core/sysaux.dbf' 
            size 10M
            autoextend on 
            next 10M 
            maxsize unlimited
  undo tablespace undo
            datafile '/usr/lib/oracle/xe/oradata/escidoc-core/undo.dbf'
            size 10M
  default temporary tablespace temp
            tempfile '/usr/lib/oracle/xe/oradata/escidoc-core/temp.dbf'
            size 10M;

create database fedora3
  logfile   group 1 ('/usr/lib/oracle/xe/oradata/fedora3/redo1.log') size 10M,
            group 2 ('/usr/lib/oracle/xe/oradata/fedora3/redo2.log') size 10M,
            group 3 ('/usr/lib/oracle/xe/oradata/fedora3/redo3.log') size 10M
  character set          utf8
  national character set utf8
  datafile '/usr/lib/oracle/xe/oradata/fedora3/system.dbf' 
            size 50M
            autoextend on 
            next 10M maxsize unlimited
            extent management local
  sysaux datafile '/usr/lib/oracle/xe/oradata/fedora3/sysaux.dbf' 
            size 10M
            autoextend on 
            next 10M 
            maxsize unlimited
  undo tablespace undo
            datafile '/usr/lib/oracle/xe/oradata/fedora3/undo.dbf'
            size 10M
  default temporary tablespace temp
            tempfile '/usr/lib/oracle/xe/oradata/fedora3/temp.dbf'
            size 10M;

create database triples
  logfile   group 1 ('/usr/lib/oracle/xe/oradata/riTriples/redo1.log') size 10M,
            group 2 ('/usr/lib/oracle/xe/oradata/riTriples/redo2.log') size 10M,
            group 3 ('/usr/lib/oracle/xe/oradata/riTriples/redo3.log') size 10M
  character set          WE8ISO8859P1
  national character set WE8ISO8859P1
  datafile '/usr/lib/oracle/xe/oradata/riTriples/system.dbf' 
            size 50M
            autoextend on 
            next 10M maxsize unlimited
            extent management local
  sysaux datafile '/usr/lib/oracle/xe/oradata/riTriples/sysaux.dbf' 
            size 10M
            autoextend on 
            next 10M 
            maxsize unlimited
  undo tablespace undo
            datafile '/usr/lib/oracle/xe/oradata/riTriples/undo.dbf'
            size 10M
  default temporary tablespace temp
            tempfile '/usr/lib/oracle/xe/oradata/riTriples/temp.dbf'
            size 10M;

      
      