INSERT INTO sm.scopes (id, name, scope_type, creator_id, creation_date, modified_by_id, last_modification_date) 
VALUES 
('escidoc:scope3','Test Scope for JUnit', 'normal', '${escidoc.creator.user}', CURRENT_TIMESTAMP, '${escidoc.creator.user}', CURRENT_TIMESTAMP);

INSERT INTO sm.scopes (id, name, scope_type, creator_id, creation_date, modified_by_id, last_modification_date) 
VALUES 
('escidoc:scope4','Test Scope1 for JUnit', 'normal', '${escidoc.creator.user}', CURRENT_TIMESTAMP, '${escidoc.creator.user}', CURRENT_TIMESTAMP);

INSERT INTO sm.statistic_data (id, xml_data, scope_id, timemarker) 
VALUES
('escidoc:statisticdata1', '<?xml version="1.0" encoding="UTF-8"?>
<statistic-record>
    <scope objid="escidoc:scope4"/>
    <parameter name="reporttest">
        <decimalvalue>1</decimalvalue>
    </parameter>
    <parameter name="reporttest1">
        <decimalvalue>1</decimalvalue>
    </parameter>
    <parameter name="time">
        <datevalue>
        1980-01-28T12:00:00
        </datevalue>
    </parameter>
    <parameter name="page">
        <stringvalue>
        createItem
        </stringvalue>
    </parameter>
    <parameter name="session_id">
        <stringvalue>
        Session1
        </stringvalue>
    </parameter>
</statistic-record>', 'escidoc:scope4', TO_DATE('2000-01-01 08:00:00','YYYY-MM-DD HH24:MI:SS'));

INSERT INTO sm.statistic_data (id, xml_data, scope_id, timemarker) 
VALUES
('escidoc:statisticdata2', '<?xml version="1.0" encoding="UTF-8"?>
<statistic-record>
    <scope objid="escidoc:scope4"/>
    <parameter name="reporttest">
        <decimalvalue>2</decimalvalue>
    </parameter>
    <parameter name="reporttest1">
        <decimalvalue>2</decimalvalue>
    </parameter>
    <parameter name="time">
        <datevalue>1980-01-28T12:00:00</datevalue>
    </parameter>
    <parameter name="page">
        <stringvalue>retrieveItem</stringvalue>
    </parameter>
    <parameter name="session_id">
        <stringvalue>Session1</stringvalue>
    </parameter>
</statistic-record>', 'escidoc:scope4', TO_DATE('2000-01-01 08:00:00','YYYY-MM-DD HH24:MI:SS'));

INSERT INTO sm.statistic_data (id, xml_data, scope_id, timemarker) 
VALUES
('escidoc:statisticdata3', '<?xml version="1.0" encoding="UTF-8"?>
<statistic-record>
    <scope objid="escidoc:scope4"/>
    <parameter name="reporttest">
        <decimalvalue>3</decimalvalue>
    </parameter>
    <parameter name="reporttest1">
        <decimalvalue>3</decimalvalue>
    </parameter>
    <parameter name="time">
        <datevalue>1980-01-28T12:00:00</datevalue>
    </parameter>
    <parameter name="page">
        <stringvalue>retrieveItem</stringvalue>
    </parameter>
    <parameter name="session_id">
        <stringvalue>Session2</stringvalue>
    </parameter>
</statistic-record>', 'escidoc:scope4', TO_DATE('2000-01-01 08:00:00','YYYY-MM-DD HH24:MI:SS'));

INSERT INTO sm.statistic_data (id, xml_data, scope_id, timemarker) 
VALUES
('escidoc:statisticdata4', '<?xml version="1.0" encoding="UTF-8"?>
<statistic-record>
    <scope objid="escidoc:scope4"/>
    <parameter name="reporttest">
        <decimalvalue>4</decimalvalue>
    </parameter>
    <parameter name="reporttest1">
        <decimalvalue>4</decimalvalue>
    </parameter>
    <parameter name="time">
        <datevalue>1980-02-28T12:00:00</datevalue>
    </parameter>
    <parameter name="page">
        <stringvalue>createItem</stringvalue>
    </parameter>
    <parameter name="session_id">
        <stringvalue>Session2</stringvalue>
    </parameter>
</statistic-record>', 'escidoc:scope4', TO_DATE('2000-01-01 08:00:00','YYYY-MM-DD HH24:MI:SS'));

INSERT INTO sm.statistic_data (id, xml_data, scope_id, timemarker) 
VALUES
('escidoc:statisticdata5', '<?xml version="1.0" encoding="UTF-8"?>
<statistic-record>
    <scope objid="escidoc:scope4"/>
    <parameter name="reporttest">
        <decimalvalue>5</decimalvalue>
    </parameter>
    <parameter name="reporttest1">
        <decimalvalue>5</decimalvalue>
    </parameter>
    <parameter name="time">
        <datevalue>1980-02-28T12:00:00</datevalue>
    </parameter>
    <parameter name="page">
        <stringvalue>homepage</stringvalue>
    </parameter>
    <parameter name="session_id">
        <stringvalue>Session2</stringvalue>
    </parameter>
</statistic-record>', 'escidoc:scope4', TO_DATE('2000-01-01 08:00:00','YYYY-MM-DD HH24:MI:SS'));

INSERT INTO sm.statistic_data (id, xml_data, scope_id, timemarker) 
VALUES
('escidoc:statisticdata6', '<?xml version="1.0" encoding="UTF-8"?>
<statistic-record>
    <scope objid="escidoc:scope4"/>
    <parameter name="reporttest">
        <decimalvalue>6</decimalvalue>
    </parameter>
    <parameter name="reporttest1">
        <decimalvalue>6</decimalvalue>
    </parameter>
    <parameter name="time">
        <datevalue>1980-03-28T12:00:00</datevalue>
    </parameter>
    <parameter name="page">
        <stringvalue>createItem</stringvalue>
    </parameter>
    <parameter name="session_id">
        <stringvalue>Session1</stringvalue>
    </parameter>
</statistic-record>', 'escidoc:scope4', TO_DATE('2000-01-01 08:00:00','YYYY-MM-DD HH24:MI:SS'));

INSERT INTO sm.statistic_data (id, xml_data, scope_id, timemarker) 
VALUES
('escidoc:statisticdata7', '<?xml version="1.0" encoding="UTF-8"?>
<statistic-record>
    <scope objid="escidoc:scope4"/>
    <parameter name="reporttest">
        <decimalvalue>7</decimalvalue>
    </parameter>
    <parameter name="reporttest1">
        <decimalvalue>7</decimalvalue>
    </parameter>
    <parameter name="time">
        <datevalue>
        1980-01-28T12:00:00
        </datevalue>
    </parameter>
    <parameter name="page">
        <stringvalue>
        createItem
        </stringvalue>
    </parameter>
    <parameter name="session_id">
        <stringvalue>
        Session1
        </stringvalue>
    </parameter>
</statistic-record>', 'escidoc:scope4', TO_DATE('2000-01-02 08:00:00','YYYY-MM-DD HH24:MI:SS'));

INSERT INTO sm.statistic_data (id, xml_data, scope_id, timemarker) 
VALUES
('escidoc:statisticdata8', '<?xml version="1.0" encoding="UTF-8"?>
<statistic-record>
    <scope objid="escidoc:scope4"/>
    <parameter name="reporttest">
        <decimalvalue>8</decimalvalue>
    </parameter>
    <parameter name="reporttest1">
        <decimalvalue>8</decimalvalue>
    </parameter>
    <parameter name="time">
        <datevalue>1980-01-28T12:00:00</datevalue>
    </parameter>
    <parameter name="page">
        <stringvalue>retrieveItem</stringvalue>
    </parameter>
    <parameter name="session_id">
        <stringvalue>Session1</stringvalue>
    </parameter>
</statistic-record>', 'escidoc:scope4', TO_DATE('2000-01-02 08:00:00','YYYY-MM-DD HH24:MI:SS'));

INSERT INTO sm.statistic_data (id, xml_data, scope_id, timemarker) 
VALUES
('escidoc:statisticdata9', '<?xml version="1.0" encoding="UTF-8"?>
<statistic-record>
    <scope objid="escidoc:scope4"/>
    <parameter name="reporttest">
        <decimalvalue>9</decimalvalue>
    </parameter>
    <parameter name="reporttest1">
        <decimalvalue>9</decimalvalue>
    </parameter>
    <parameter name="time">
        <datevalue>1980-01-28T12:00:00</datevalue>
    </parameter>
    <parameter name="page">
        <stringvalue>retrieveItem</stringvalue>
    </parameter>
    <parameter name="session_id">
        <stringvalue>Session2</stringvalue>
    </parameter>
</statistic-record>', 'escidoc:scope4', TO_DATE('2000-01-02 08:00:00','YYYY-MM-DD HH24:MI:SS'));

INSERT INTO sm.statistic_data (id, xml_data, scope_id, timemarker) 
VALUES
('escidoc:statisticdata10', '<?xml version="1.0" encoding="UTF-8"?>
<statistic-record>
    <scope objid="escidoc:scope4"/>
    <parameter name="reporttest">
        <decimalvalue>10</decimalvalue>
    </parameter>
    <parameter name="reporttest1">
        <decimalvalue>10</decimalvalue>
    </parameter>
    <parameter name="time">
        <datevalue>1980-02-28T12:00:00</datevalue>
    </parameter>
    <parameter name="page">
        <stringvalue>createItem</stringvalue>
    </parameter>
    <parameter name="session_id">
        <stringvalue>Session2</stringvalue>
    </parameter>
</statistic-record>', 'escidoc:scope4', TO_DATE('2000-01-02 08:00:00','YYYY-MM-DD HH24:MI:SS'));

INSERT INTO sm.statistic_data (id, xml_data, scope_id, timemarker) 
VALUES
('escidoc:statisticdata11', '<?xml version="1.0" encoding="UTF-8"?>
<statistic-record>
    <scope objid="escidoc:scope4"/>
    <parameter name="reporttest">
        <decimalvalue>11</decimalvalue>
    </parameter>
    <parameter name="reporttest1">
        <decimalvalue>11</decimalvalue>
    </parameter>
    <parameter name="time">
        <datevalue>1980-02-28T12:00:00</datevalue>
    </parameter>
    <parameter name="page">
        <stringvalue>homepage</stringvalue>
    </parameter>
    <parameter name="session_id">
        <stringvalue>Session2</stringvalue>
    </parameter>
</statistic-record>', 'escidoc:scope4', TO_DATE('2000-01-02 08:00:00','YYYY-MM-DD HH24:MI:SS'));

INSERT INTO sm.statistic_data (id, xml_data, scope_id, timemarker) 
VALUES
('escidoc:statisticdata12', '<?xml version="1.0" encoding="UTF-8"?>
<statistic-record>
    <scope objid="escidoc:scope4"/>
    <parameter name="reporttest">
        <decimalvalue>12</decimalvalue>
    </parameter>
    <parameter name="reporttest1">
        <decimalvalue>12</decimalvalue>
    </parameter>
    <parameter name="time">
        <datevalue>1980-03-28T12:00:00</datevalue>
    </parameter>
    <parameter name="page">
        <stringvalue>createItem</stringvalue>
    </parameter>
    <parameter name="session_id">
        <stringvalue>Session1</stringvalue>
    </parameter>
</statistic-record>', 'escidoc:scope4', TO_DATE('2000-01-02 08:00:00','YYYY-MM-DD HH24:MI:SS'));

INSERT INTO sm.statistic_data (id, xml_data, scope_id, timemarker) 
VALUES
('escidoc:statisticdata13', '<?xml version="1.0" encoding="UTF-8"?>
<statistic-record>
    <scope objid="escidoc:scope4"/>
    <parameter name="reporttest">
        <decimalvalue>13</decimalvalue>
    </parameter>
    <parameter name="reporttest1">
        <decimalvalue>13</decimalvalue>
    </parameter>
    <parameter name="time">
        <datevalue>
        1980-01-28T12:00:00
        </datevalue>
    </parameter>
    <parameter name="page">
        <stringvalue>
        createItem
        </stringvalue>
    </parameter>
    <parameter name="session_id">
        <stringvalue>
        Session1
        </stringvalue>
    </parameter>
</statistic-record>', 'escidoc:scope4', TO_DATE('2009-02-01 08:00:00','YYYY-MM-DD HH24:MI:SS'));

INSERT INTO sm.statistic_data (id, xml_data, scope_id, timemarker) 
VALUES
('escidoc:statisticdata14', '<?xml version="1.0" encoding="UTF-8"?>
<statistic-record>
    <scope objid="escidoc:scope4"/>
    <parameter name="reporttest">
        <decimalvalue>14</decimalvalue>
    </parameter>
    <parameter name="reporttest1">
        <decimalvalue>14</decimalvalue>
    </parameter>
    <parameter name="time">
        <datevalue>1980-01-28T12:00:00</datevalue>
    </parameter>
    <parameter name="page">
        <stringvalue>retrieveItem</stringvalue>
    </parameter>
    <parameter name="session_id">
        <stringvalue>Session1</stringvalue>
    </parameter>
</statistic-record>', 'escidoc:scope4', TO_DATE('2009-02-01 08:00:00','YYYY-MM-DD HH24:MI:SS'));

INSERT INTO sm.statistic_data (id, xml_data, scope_id, timemarker) 
VALUES
('escidoc:statisticdata15', '<?xml version="1.0" encoding="UTF-8"?>
<statistic-record>
    <scope objid="escidoc:scope4"/>
    <parameter name="reporttest">
        <decimalvalue>15</decimalvalue>
    </parameter>
    <parameter name="reporttest1">
        <decimalvalue>15</decimalvalue>
    </parameter>
    <parameter name="time">
        <datevalue>1980-01-28T12:00:00</datevalue>
    </parameter>
    <parameter name="page">
        <stringvalue>retrieveItem</stringvalue>
    </parameter>
    <parameter name="session_id">
        <stringvalue>Session2</stringvalue>
    </parameter>
</statistic-record>', 'escidoc:scope4', TO_DATE('2009-02-01 08:00:00','YYYY-MM-DD HH24:MI:SS'));

INSERT INTO sm.statistic_data (id, xml_data, scope_id, timemarker) 
VALUES
('escidoc:statisticdata16', '<?xml version="1.0" encoding="UTF-8"?>
<statistic-record>
    <scope objid="escidoc:scope4"/>
    <parameter name="reporttest">
        <decimalvalue>16</decimalvalue>
    </parameter>
    <parameter name="reporttest1">
        <decimalvalue>16</decimalvalue>
    </parameter>
    <parameter name="time">
        <datevalue>1980-02-28T12:00:00</datevalue>
    </parameter>
    <parameter name="page">
        <stringvalue>createItem</stringvalue>
    </parameter>
    <parameter name="session_id">
        <stringvalue>Session2</stringvalue>
    </parameter>
</statistic-record>', 'escidoc:scope4', TO_DATE('2009-02-01 08:00:00','YYYY-MM-DD HH24:MI:SS'));

INSERT INTO sm.statistic_data (id, xml_data, scope_id, timemarker) 
VALUES
('escidoc:statisticdata17', '<?xml version="1.0" encoding="UTF-8"?>
<statistic-record>
    <scope objid="escidoc:scope4"/>
    <parameter name="reporttest">
        <decimalvalue>17</decimalvalue>
    </parameter>
    <parameter name="reporttest1">
        <decimalvalue>17</decimalvalue>
    </parameter>
    <parameter name="time">
        <datevalue>1980-02-28T12:00:00</datevalue>
    </parameter>
    <parameter name="page">
        <stringvalue>homepage</stringvalue>
    </parameter>
    <parameter name="session_id">
        <stringvalue>Session2</stringvalue>
    </parameter>
</statistic-record>', 'escidoc:scope4', TO_DATE('2009-02-01 08:00:00','YYYY-MM-DD HH24:MI:SS'));

INSERT INTO sm.statistic_data (id, xml_data, scope_id, timemarker) 
VALUES
('escidoc:statisticdata18', '<?xml version="1.0" encoding="UTF-8"?>
<statistic-record>
    <scope objid="escidoc:scope4"/>
    <parameter name="reporttest">
        <decimalvalue>18</decimalvalue>
    </parameter>
    <parameter name="reporttest1">
        <decimalvalue>18</decimalvalue>
    </parameter>
    <parameter name="time">
        <datevalue>1980-03-28T12:00:00</datevalue>
    </parameter>
    <parameter name="page">
        <stringvalue>createItem</stringvalue>
    </parameter>
    <parameter name="session_id">
        <stringvalue>Session1</stringvalue>
    </parameter>
</statistic-record>', 'escidoc:scope4', TO_DATE('2009-02-01 08:00:00','YYYY-MM-DD HH24:MI:SS'));

