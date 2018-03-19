package com.zhuanche.shiro.session;

import java.io.Serializable;
import java.util.UUID;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;

public class UuIdSessionIdGenerator implements SessionIdGenerator{

	@Override
	public Serializable generateId(Session session) {
		
		return UUID.randomUUID().toString().replace("-", "").toUpperCase();
	}

}
