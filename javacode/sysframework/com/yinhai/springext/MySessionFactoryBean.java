package com.yinhai.springext;

import java.util.ArrayList;

import org.springframework.core.io.Resource;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;

public class MySessionFactoryBean extends LocalSessionFactoryBean {

	private Resource[] mappingLocationsExtends;

	public void setMappingLocationsExtends(Resource... mappingLocationsExtends) {
		this.mappingLocationsExtends = mappingLocationsExtends;
	}

	public void setMappingLocations(Resource... mappingLocations) {
		if (mappingLocationsExtends != null) {
			ArrayList<Resource> realList = new ArrayList<Resource>();
			label117: for (Resource res : mappingLocations) {
				for (Resource resExt : mappingLocationsExtends) {
					String extFileName = resExt.getFilename();
					if (extFileName != null && extFileName.equals(res.getFilename())) {
						realList.add(resExt);
						break label117;
					}
				}
				realList.add(res);
			}
			super.setMappingLocations((Resource[]) realList.toArray(new Resource[realList.size()]));
		} else {
			super.setMappingLocations(mappingLocations);
		}
	}
}
