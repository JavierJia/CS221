/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package util;

import java.io.Serializable;

/**
 * @author Yasser Ganjisaffar <lastname at gmail dot com>
 * Changed by Jianfeng
 */

public class WebURL implements Serializable {

	private static final long serialVersionUID = 1L;

	private String url;

	private int depth;
	private String domain;
	private String subDomain;
	private String path;

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		WebURL otherUrl = (WebURL) o;
		return url != null && url.equals(otherUrl.getURL());

	}

	@Override
	public String toString() {
		return url;
	}
	
	public WebURL(String url){
		setURL(url);
	}

	/**
	 * Returns the Url string
	 */
	public String getURL() {
		return url;
	}

	public void setURL(String url) {
		this.url = url;

		int domainStartIdx = url.indexOf("//") + 2;
		int domainEndIdx = url.indexOf('/', domainStartIdx);
		domain = url.substring(domainStartIdx, domainEndIdx);
		subDomain = "";
		String[] parts = domain.split("\\.");
		if (parts.length > 2) {
			domain = parts[parts.length - 2] + "." + parts[parts.length - 1];
			int limit = 2;
			for (int i = 0; i < parts.length - limit; i++) {
				if (subDomain.length() > 0) {
					subDomain += ".";
				}
				subDomain += parts[i];
			}
		}
		path = url.substring(domainEndIdx);
		int pathEndIdx = path.indexOf('?');
		if (pathEndIdx >= 0) {
			path = path.substring(0, pathEndIdx);
		}
		depth = 0;
		String [] split =  path.split("/");
		if (split != null){
			depth += split.length;
		}
	}


	public String getPernalSite(){
		String [] split =  path.split("/");
		String rstr = getFullDomain();
		if (split != null && split.length > 0){
			rstr += split[0] + "/";
		}
		return rstr;
	}
	/**
	 * Returns the crawl depth at which this Url is first observed. Seed Urls
	 * are at depth 0. Urls that are extracted from seed Urls are at depth 1,
	 * etc.
	 */
	public int getDepth() {
		return depth;
	}

	/**
	 * Returns the domain of this Url. For 'http://www.example.com/sample.htm',
	 * domain will be 'example.com'
	 */
	public String getDomain() {
		return domain;
	}

	public String getSubDomain() {
		return subDomain;
	}
	
	public String getFullDomain(){
		return "http://" + subDomain + "." + domain + "/";
	}

	/**
	 * Returns the path of this Url. For 'http://www.example.com/sample.htm',
	 * domain will be 'sample.htm'
	 */
	public String getPath() {
		return path;
	}
	
	public static void main (String argv[]){
		WebURL url = new WebURL("http://mlearn.ics.uci.edu/MLOther.html");
		System.out.println(url.getPath());
		System.out.println(url.getDepth());
		System.out.println(url.getDomain());
		System.out.println(url.getSubDomain());
		System.out.println(url.getFullDomain());
	}
	
}
