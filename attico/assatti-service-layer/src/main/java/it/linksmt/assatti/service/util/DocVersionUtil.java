package it.linksmt.assatti.service.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.linksmt.assatti.datalayer.domain.ICmisDoc;

public class DocVersionUtil {
	public static List<ICmisDoc> getLatestVersionList(Collection<?> docs){
		List<ICmisDoc> latestVersionList = null;
		if(docs!=null) {
			latestVersionList = new ArrayList<ICmisDoc>();
			Map<String, List<ICmisDoc>> map = new HashMap<String, List<ICmisDoc>>();
			for(Object docObj : docs) {
				ICmisDoc doc = (ICmisDoc)docObj;
				if(doc.getCmisDocumentId()!=null) {
					String idUnversioned = doc.getCmisDocumentId().substring(0, doc.getCmisDocumentId().trim().indexOf("\u003B"));
					if(!map.containsKey(idUnversioned)) {
						map.put(idUnversioned, new ArrayList<ICmisDoc>());
					}
					map.get(idUnversioned).add(doc);
				}
			}
			for(String idUnversioned : map.keySet()) {
				if(map.get(idUnversioned) != null && map.get(idUnversioned).size() > 0) {
					if(map.get(idUnversioned).size() == 1) {
						latestVersionList.add(map.get(idUnversioned).get(0));
					}else if(map.get(idUnversioned).size() > 1){
						Map<Integer, ICmisDoc> versionDocMap = new HashMap<Integer, ICmisDoc>();
						for(ICmisDoc doc : map.get(idUnversioned)) {
							versionDocMap.put(Integer.parseInt(doc.getCmisDocumentId().trim().split("\u003B")[1].split("\\.")[1]), doc);
						}
						List<Integer> versions = new ArrayList<Integer>();
						versions.addAll(versionDocMap.keySet());
						Collections.sort(versions, Collections.reverseOrder());
						Integer latestVersion = versions.iterator().next();
						latestVersionList.add(versionDocMap.get(latestVersion));
					}
				}
			}
		}
		return latestVersionList;
	}
}
