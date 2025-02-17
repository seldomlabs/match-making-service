
package com.matchmaker.util;

import org.apache.commons.codec.DecoderException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.solr.common.util.Base64;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.*;

public class RedisUtils
{
	
	private static final Logger logger = LogManager.getLogger(RedisUtils.class);

	/**
	 * Utility method for converting from Redis String value to a Java BitSet
	 * correctly preserving Byte Order More info :
	 * http://grantmuller.com/be-careful-with-your-redis-bitsets-and-java/
	 * 
	 * @param bytes
	 * @return
	 */
	public static BitSet fromByteArrayReverse(final String base64)
	{
		final BitSet bits = new BitSet();
		byte[] bytes = Base64.base64ToByteArray(base64);
		for (int i = 0; i < bytes.length * 8; i++)
		{
			if ((bytes[i / 8] & (1 << (7 - (i % 8)))) != 0)
			{
				bits.set(i);
			}
		}
		return bits;
	}
	
	/**
	 * Utility method for converting from Java BitSet to a Redis String value
	 * correctly preserving Byte Order More info :
	 * http://grantmuller.com/be-careful-with-your-redis-bitsets-and-java/
	 * 
	 * @param bits
	 * @return
	 */
	public static String toByteArrayReverse(final BitSet bits)
	{
		final byte[] bytes = new byte[bits.length() / 8 + 1];
		for (int i = 0; i < bits.length(); i++)
		{
			if (bits.get(i))
			{
				final int value = bytes[i / 8] | (1 << (7 - (i % 8)));
				bytes[i / 8] = (byte) value;
			}
		}
		return Base64.byteArrayToBase64(bytes);
	}
	
	public static <T> List<T> getFilteredList(List<T> inpList, BitSet bs, int offset)
	{
		if (bs == null)
			return inpList;
		List<T> outList = new ArrayList<>();
		for (int i = offset; i < (inpList.size()+offset); i++)
		{
			if (bs.get(i))
			{
				outList.add(inpList.get(i-offset));
			}
		}
		return outList;
	}
	
	/**
	 * Converts hgetall response to map of long , set<long>
	 * Used for checking belongingness to a specific collection 
	 * @return
	 */
	public static Map<Long,Set<Long>> convertToCheckListMap(Map<String,String> resp) {
		Map<Long,Set<Long>> checkListMap = new HashMap<>();
		if (resp != null && !resp.isEmpty()) {
			for (Map.Entry<String, String> e : resp.entrySet()) {
				  try {
					  Long k = Long.parseLong(e.getKey());
					  String[] vals = e.getValue().split("\\s*,\\s*");
					  Set<Long> users = new HashSet<>();
					  for (String v : vals) {
						  try {
							  users.add(Long.parseLong(v));
						  }
						  catch (Exception ex) {
							  logger.error("Parse error: ", ex);
						  }
					  }
					  if (!users.isEmpty()) {
						  checkListMap.put(k, users);
					  }
				  }
				  catch (Exception ex) {
					  logger.error("Parse error: ", ex);
				  }
			  }
		}
		return checkListMap;
	}
	
	public static JedisPool getConfiguredJedisPoolTest(String url, int port) {
        logger.info("Configuring Jedis Pool");
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(50);
        config.setMaxIdle(20);
        config.setMinIdle(1);
        config.setMaxWaitMillis(300000000);
        return new JedisPool(config, url, port);
    }
	
	public static void main(String[] args) throws DecoderException
	{
		BitSet b = new BitSet(12);
		b.set(0, 4, true);
		b.set(10, 11, true);

		String value = toByteArrayReverse(b);
		System.out.println(value);
		BitSet c = fromByteArrayReverse(value);
		System.out.println(c.get(11));
		System.out.println(c.cardinality());
		
		String value1 = "AAAH///+";
        BitSet x = fromByteArrayReverse(value1);
        for(int i=0; i<x.cardinality(); i++) {
            System.out.print(x.get(i) ? 1 : 0);
        }
	}
}
