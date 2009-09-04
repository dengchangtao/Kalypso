<?xml version="1.0" encoding="UTF-8"?>
<StyledLayerDescriptor version="String" xmlns="http://www.opengis.net/sld" xmlns:gml="http://www.opengis.net/gml" xmlns:ogc="http://www.opengis.net/ogc" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <NamedLayer>
    <Name>deegree style definition</Name>
    <UserStyle>
      <Name>Subcatchments</Name>
      <Title>Subcatchments</Title>
      <IsDefault>1</IsDefault>
      <FeatureTypeStyle>
        <Name>Subcatchment</Name>
        <Rule>
          <Name>default</Name>
          <Title>Subcatchments</Title>
          <Abstract>default</Abstract>
          <ogc:Filter xmlns:ogc="http://www.opengis.net/ogc">
            <ogc:PropertyIsLike escape="\" singleChar="?" wildCard="*">
              <ogc:PropertyName>kurzzeit</ogc:PropertyName>
              <ogc:Literal>wasserwerk.kz</ogc:Literal>
            </ogc:PropertyIsLike>
          </ogc:Filter>
<MinScaleDenominator>0.0</MinScaleDenominator>
          <MaxScaleDenominator>1.7976931348623157E308</MaxScaleDenominator>
          <PolygonSymbolizer>
            <Geometry>
              <ogc:PropertyName>Ort</ogc:PropertyName>
            </Geometry>
            <Fill>
              <CssParameter name="fill-opacity">0.3</CssParameter>
              <CssParameter name="fill">#c0c0c0</CssParameter>
            </Fill>
            <Stroke>
              <CssParameter name="stroke">#000000</CssParameter>
              <CssParameter name="stroke-width">1.0</CssParameter>
              <CssParameter name="stroke-linejoin">mitre</CssParameter>
              <CssParameter name="stroke-opacity">1.0</CssParameter>
              <CssParameter name="stroke-linecap">butt</CssParameter>
            </Stroke>
          </PolygonSymbolizer>
        </Rule>
        <Rule>
          <Name>default</Name>
          <Title>default</Title>
          <Abstract>default</Abstract>
          <ogc:Filter xmlns:ogc="http://www.opengis.net/ogc">
            <ogc:PropertyIsLike escape="\" singleChar="?" wildCard="*">
              <ogc:PropertyName>kurzzeit</ogc:PropertyName>
              <ogc:Literal>DB.kz</ogc:Literal>
            </ogc:PropertyIsLike>
          </ogc:Filter>
<MinScaleDenominator>0.0</MinScaleDenominator>
          <MaxScaleDenominator>1.7976931348623157E308</MaxScaleDenominator>
          <PolygonSymbolizer>
            <Geometry>
              <ogc:PropertyName>Ort</ogc:PropertyName>
            </Geometry>
            <Fill>
              <CssParameter name="fill-opacity">1.0</CssParameter>
              <CssParameter name="fill">#5aa5a5</CssParameter>
            </Fill>
            <Stroke>
              <CssParameter name="stroke">#000000</CssParameter>
              <CssParameter name="stroke-width">1.0</CssParameter>
              <CssParameter name="stroke-linejoin">mitre</CssParameter>
              <CssParameter name="stroke-opacity">1.0</CssParameter>
              <CssParameter name="stroke-linecap">butt</CssParameter>
            </Stroke>
          </PolygonSymbolizer>
        </Rule>
        <Rule>
          <Name>default</Name>
          <Title>default</Title>
          <Abstract>default</Abstract>
          <ogc:Filter xmlns:ogc="http://www.opengis.net/ogc">
            <ogc:PropertyIsLike escape="\" singleChar="?" wildCard="*">
              <ogc:PropertyName>kurzzeit</ogc:PropertyName>
              <ogc:Literal>Bauhof.kz</ogc:Literal>
            </ogc:PropertyIsLike>
          </ogc:Filter>
<MinScaleDenominator>0.0</MinScaleDenominator>
          <MaxScaleDenominator>1.7976931348623157E308</MaxScaleDenominator>
          <PolygonSymbolizer>
            <Geometry>
              <ogc:PropertyName>Ort</ogc:PropertyName>
            </Geometry>
            <Fill>
              <CssParameter name="fill-opacity">1.0</CssParameter>
              <CssParameter name="fill">#686c97</CssParameter>
            </Fill>
            <Stroke>
              <CssParameter name="stroke">#000000</CssParameter>
              <CssParameter name="stroke-width">1.0</CssParameter>
              <CssParameter name="stroke-linejoin">mitre</CssParameter>
              <CssParameter name="stroke-opacity">1.0</CssParameter>
              <CssParameter name="stroke-linecap">butt</CssParameter>
            </Stroke>
          </PolygonSymbolizer>
        </Rule>
        <Rule>
          <Name>default</Name>
          <Title>default</Title>
          <Abstract>default</Abstract>
          <MinScaleDenominator>0.0</MinScaleDenominator>
          <MaxScaleDenominator>1.7976931348623157E308</MaxScaleDenominator>
        </Rule>
        <Rule>
          <Name>default</Name>
          <Title>default</Title>
          <Abstract>default</Abstract>
          <ogc:Filter xmlns:ogc="http://www.opengis.net/ogc">
            <ogc:PropertyIsLike escape="\" singleChar="?" wildCard="*">
              <ogc:PropertyName>kurzzeit</ogc:PropertyName>
              <ogc:Literal>Desy.kz</ogc:Literal>
            </ogc:PropertyIsLike>
          </ogc:Filter>
<MinScaleDenominator>0.0</MinScaleDenominator>
          <MaxScaleDenominator>1.7976931348623157E308</MaxScaleDenominator>
          <PolygonSymbolizer>
            <Geometry>
              <ogc:PropertyName>Ort</ogc:PropertyName>
            </Geometry>
            <Fill>
              <CssParameter name="fill-opacity">1.0</CssParameter>
              <CssParameter name="fill">#808080</CssParameter>
            </Fill>
            <Stroke>
              <CssParameter name="stroke">#000000</CssParameter>
              <CssParameter name="stroke-width">1.0</CssParameter>
              <CssParameter name="stroke-linejoin">mitre</CssParameter>
              <CssParameter name="stroke-opacity">1.0</CssParameter>
              <CssParameter name="stroke-linecap">butt</CssParameter>
            </Stroke>
          </PolygonSymbolizer>
        </Rule>
      </FeatureTypeStyle>
    </UserStyle>
  </NamedLayer>
</StyledLayerDescriptor>
