<?xml version="1.0" encoding="WINDOWS-1252"?>
<c1d2d:ControlModelGroup xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:gml="http://www.opengis.net/gml" xmlns:xs="http://www.w3.org/2001/XMLSchema-instance" xmlns:c1d2d="http://www.tu-harburg.de/wb/kalypso/schemata/1d2dControl" xmlns:swe="http://www.opengis.net/swe" xmlns:sweExt="org.kalypso.swe.ext" gml:id="root">
 <c1d2d:controlModelCollection>
  <c1d2d:ControlModelCollection gml:id="ControlModelCollection11885498675311">
   <c1d2d:activeModelID xlink:href="#ControlModel11885498675621"/>
   <c1d2d:controlModelMember>
    <c1d2d:ControlModel gml:id="ControlModel11885498675621">
     <gml:name>Control modell f�r test2d</gml:name>
     <wb1d2d:calculationUnit xmlns:wb1d2d="http://www.tu-harburg.de/wb/kalypso/schemata/1d2d" xlink:href="discretisation.gml#CalculationUnit2D118854986746823292"/>
     <c1d2d:Version>NEW</c1d2d:Version>
     <c1d2d:VEGETA>true</c1d2d:VEGETA>
     <c1d2d:IDNOPT>-1</c1d2d:IDNOPT>
     <c1d2d:startsim/>
     <c1d2d:IEDSW>10</c1d2d:IEDSW>
     <c1d2d:TBFACT>0.2</c1d2d:TBFACT>
     <c1d2d:TBMIN>2.0</c1d2d:TBMIN>
     <c1d2d:_p_bottom/>
     <c1d2d:OMEGA>54.0</c1d2d:OMEGA>
     <c1d2d:ELEV>8.4</c1d2d:ELEV>
     <c1d2d:UNOM>0.5</c1d2d:UNOM>
     <c1d2d:UDIR>235.0</c1d2d:UDIR>
     <c1d2d:HMIN>0.15</c1d2d:HMIN>
     <c1d2d:DSET>0.1</c1d2d:DSET>
     <c1d2d:DSETD>0.12</c1d2d:DSETD>
     <c1d2d:_steady>true</c1d2d:_steady>
     <c1d2d:_unsteady>false</c1d2d:_unsteady>
     <c1d2d:NITI>15</c1d2d:NITI>
     <c1d2d:NITN>1</c1d2d:NITN>
     <c1d2d:CONV_1>0.01</c1d2d:CONV_1>
     <c1d2d:CONV_2>0.01</c1d2d:CONV_2>
     <c1d2d:CONV_3>0.0010</c1d2d:CONV_3>
     <c1d2d:IDRPT>1</c1d2d:IDRPT>
     <c1d2d:DRFACT>0.05</c1d2d:DRFACT>
     <c1d2d:AC1>1.5</c1d2d:AC1>
     <c1d2d:AC2>0.67</c1d2d:AC2>
     <c1d2d:AC3>0.04</c1d2d:AC3>
     <c1d2d:_restart>false</c1d2d:_restart>
     <c1d2d:IACCYC>1</c1d2d:IACCYC>
     <c1d2d:FNAM3/>
     <c1d2d:_steadyBC>0.2</c1d2d:_steadyBC>
     <c1d2d:timestepsMember>
      <c1d2d:TimestepsObservation gml:id="TimestepsObservation11885498675621">
       <gml:name>Zeitschritt Definition</gml:name>
       <om:resultDefinition xmlns:om="http://www.opengis.net/om">
        <sweExt:SortedRecordDefinition gml:id="SortedRecordDefinition11885498675931">
         <swe:component xlink:href="urn:ogc:gml:dict:kalypso:model:1d2d:timeserie:components#OrdinalNumber"/>
         <swe:component xlink:href="urn:ogc:gml:dict:kalypso:model:1d2d:timeserie:components#Time"/>
         <swe:component xlink:href="urn:ogc:gml:dict:kalypso:model:1d2d:timeserie:components#UnderRelaxationFactor"/>
         <sweExt:sortedComponent xlink:href="urn:ogc:gml:dict:kalypso:model:1d2d:timeserie:components#Time"/>
        </sweExt:SortedRecordDefinition>
       </om:resultDefinition>
       <om:result xmlns:om="http://www.opengis.net/om"></om:result>
      </c1d2d:TimestepsObservation>
     </c1d2d:timestepsMember>
    </c1d2d:ControlModel>
   </c1d2d:controlModelMember>
  </c1d2d:ControlModelCollection>
 </c1d2d:controlModelCollection>
</c1d2d:ControlModelGroup>
