/*----------------    FILE HEADER KALYPSO ------------------------------------------
 *
 *  This file is part of kalypso.
 *  Copyright (C) 2004 by:
 *
 *  Technical University Hamburg-Harburg (TUHH)
 *  Institute of River and coastal engineering
 *  Denickestra�e 22
 *  21073 Hamburg, Germany
 *  http://www.tuhh.de/wb
 *
 *  and
 *
 *  Bjoernsen Consulting Engineers (BCE)
 *  Maria Trost 3
 *  56070 Koblenz, Germany
 *  http://www.bjoernsen.de
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *  Contact:
 *
 *  E-Mail:
 *  belger@bjoernsen.de
 *  schlienger@bjoernsen.de
 *  v.doemming@tuhh.de
 *
 *  ---------------------------------------------------------------------------*/
package org.kalypso.ui.rrm.internal.timeseries.view;

import org.eclipse.jface.wizard.Wizard;
import org.kalypso.ogc.sensor.IAxis;
import org.kalypso.ui.wizard.sensor.ImportObservationData;
import org.kalypso.ui.wizard.sensor.ImportObservationSourcePage;

/**
 * @author Gernot Belger
 */
public class TimeseriesImportWizard extends Wizard
{
  private final ImportObservationData m_data;

  public TimeseriesImportWizard( final IAxis[] allowedAxes )
  {
    m_data = new ImportObservationData( allowedAxes );

    addPage( new ImportObservationSourcePage( "sourcePage", m_data ) );
  }

  @Override
  public boolean performFinish( )
  {
    // TODO Auto-generated method stub

    // TODO: create observation

    return true;
  }
}