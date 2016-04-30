package com.yihaomen.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.yihaomen.annotation.Log;

@Service(value = "myservice")
public class ComputeServiceImpl implements IComputeService {

	private static final Logger log = LoggerFactory.getLogger(ComputeServiceImpl.class);

	@Log
	public int compute(int i1, String s1, int i2, String s2) {
		log.info("in compute( " + i1 + "," + s1 + "," + i2 + "," + s2+" )");

		return i1;
	}

}
