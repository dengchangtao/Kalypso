<?xml version="1.0" encoding="UTF-8"?>
<StyledLayerDescriptor version='1.0.0'
	xmlns='http://www.opengis.net/sld' xmlns:sld='http://www.opengis.net/sld'
	xmlns:sldExt='http://www.opengis.net/sldExt' xmlns:gml='http://www.opengis.net/gml'
	xmlns:ogc='http://www.opengis.net/ogc' xmlns:xlink='http://www.w3.org/1999/xlink'
	xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>
	<NamedLayer>
		<Name>tinStyles</Name>
		<UserStyle>
			<Name>tinLineStyle</Name>
			<FeatureTypeStyle xmlns="http://www.opengis.net/sld"
				xmlns:ogc="http://www.opengis.net/ogc">
				<Name>tinFeatureTypeStyle</Name>
				<Rule>
					<Name>tinWRule</Name>
					<MinScaleDenominator>0.0</MinScaleDenominator>
					<MaxScaleDenominator>1.7976931348623157E308</MaxScaleDenominator>
					<SurfaceLineSymbolizer xmlns:sldExt="http://www.opengis.net/sldExt"
						uom="pixel">
						<Geometry>
							<ogc:PropertyName>res1d2d:triangulatedSurfaceMember
							</ogc:PropertyName>
						</Geometry>
						<sldExt:LineColorMap>
							<sldExt:LineColorMapEntry>
								<Stroke>
									<CssParameter name='stroke'>#ff0000</CssParameter>
									<CssParameter name='stroke-width'>1.0</CssParameter>
								</Stroke>
								<sldExt:label>from</sldExt:label>
								<sldExt:quantity>0.0</sldExt:quantity>
							</sldExt:LineColorMapEntry>
							<sldExt:LineColorMapEntry>
								<Stroke>
									<CssParameter name='stroke'>#ff0000</CssParameter>
									<CssParameter name='stroke-width'>1.0</CssParameter>
								</Stroke>
								<sldExt:label>to</sldExt:label>
								<sldExt:quantity>1.0</sldExt:quantity>
							</sldExt:LineColorMapEntry>
							<sldExt:LineColorMapEntry>
								<Stroke>
									<CssParameter name='stroke'>#ff0000</CssParameter>
									<CssParameter name='stroke-width'>3.0</CssParameter>
								</Stroke>
								<sldExt:label>fat</sldExt:label>
								<sldExt:quantity>0.5</sldExt:quantity>
							</sldExt:LineColorMapEntry>
						</sldExt:LineColorMap>
					</SurfaceLineSymbolizer>
				</Rule>
			</FeatureTypeStyle>
		</UserStyle>
	</NamedLayer>
</StyledLayerDescriptor>