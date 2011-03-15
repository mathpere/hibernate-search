/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2011, Red Hat, Inc. and/or its affiliates or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat, Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.hibernate.search.test.batchindexing;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * @author Bayo Erinle
 */
@Embeddable
public class LegacyCarPlantPK implements Serializable {

	private String plantId;
	private String carId;

	@Column(name = "PLANT_ID")
	public String getPlantId() {
		return plantId;
	}

	public void setPlantId(String plantId) {
		this.plantId = plantId;
	}

	@Column(name = "CAR_ID")
	public String getCarId() {
		return carId;
	}

	public void setCarId(String carId) {
		this.carId = carId;
	}

	@Override
	public boolean equals(Object o) {
		if ( this == o )
			return true;
		if ( o == null || getClass() != o.getClass() )
			return false;
		LegacyCarPlantPK that = (LegacyCarPlantPK) o;
		if ( carId != null ? !carId.equals( that.carId ) : that.carId != null )
			return false;
		if ( plantId != null ? !plantId.equals( that.plantId ) : that.plantId != null )
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = plantId != null ? plantId.hashCode() : 0;
		result = 31 * result + ( carId != null ? carId.hashCode() : 0 );
		return result;
	}
}
