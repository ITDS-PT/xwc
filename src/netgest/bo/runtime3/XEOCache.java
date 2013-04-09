package netgest.bo.runtime3;

import netgest.bo.data.DataSet;

public interface XEOCache {

	public void initialize();

	public void putObject(String className, long boui, DataSet data);

	public void putList(String className, int listHashCode, DataSet data);

	public DataSet getList(String className, int listHashCode);

	public DataSet getObject(String className, long boui);

}