package com.securityx.mef.log.mapreduce;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Maps;
import com.securityx.mef.log.mapreduce.logutils.LogSampler;

public class LogSamplerTest {
  Map<String, String> patterns = Maps.newHashMap();
  private LogSampler sampler;

  @Test
  public void testLogSamplerField() {
    this.sampler = new LogSampler("field:^(?:10\\.10|192\\.)", 0, 32768);
    Map<String, List<Object>> r = new HashMap<String, List<Object>>();
    r.put("unused", Arrays.asList(new Object[] { "192.168.1.1" }));
    r.put("field", Arrays.asList(new Object[] { "10.10.30.1" }));

    Assert.assertTrue(this.sampler.needProcess(r));
  }

  @Test
  public void testLogSamplerCommaSeparatedPattern() {
    this.sampler = new LogSampler("ip:^(?:10\\.20|192\\.|\\,),name:^cluster", 0, 32768);
    Map<String, List<Object>> r = new HashMap<String, List<Object>>();
    r.put("unused", Arrays.asList(new Object[] { "192.168.1.1" }));
    r.put("field", Arrays.asList(new Object[] { "10.10.30.1" }));
    r.put("name", Arrays.asList(new Object[] { "cluster5" }));

    Assert.assertTrue(this.sampler.needProcess(r));
  }

  @Test
  public void testLogSamplerNullPattern() {
    this.sampler = new LogSampler(null, 0, 32768);
    Map<String, List<Object>> r = new HashMap<String, List<Object>>();
    r.put("unused", Arrays.asList(new Object[] { "192.168.1.1" }));
    r.put("field", Arrays.asList(new Object[] { "192.168.1.1" }));

    Assert.assertTrue(this.sampler.needProcess(r));
  }

  @Test
  public void testLogSamplerNoSuchField() {
    this.sampler = new LogSampler("ip:^(?:10\\.10|192\\.)", 0, 32768);
    Map<String, List<Object>> r = new HashMap<String, List<Object>>();
    r.put("unused", Arrays.asList(new Object[] { "192.168.1.1" }));
    r.put("field", Arrays.asList(new Object[] { "192.168.1.1" }));

    Assert.assertTrue(this.sampler.needProcess(r));

  }

  @Test
  public void testLogSamplerMatchNegBound() {
    this.sampler = new LogSampler("field:^(?:10\\.10|192\\.)", -1,
        Integer.MAX_VALUE);
    Map<String, List<Object>> r = new HashMap<String, List<Object>>();
    r.put("unused", Arrays.asList(new Object[] { "192.168.1.1" }));
    r.put("ip", Arrays.asList(new Object[] { "10.10.30.51" }));

    Assert.assertTrue(this.sampler.needProcess(r));

  }

  @Test
  public void testLogSamplerMatch() {
    this.sampler = new LogSampler("ip:^(?:10\\.10|192\\.)", 23372, 23373);
    Map<String, List<Object>> r = new HashMap<String, List<Object>>();
    r.put("unused", Arrays.asList(new Object[] { "192.168.1.1" }));
    r.put("ip", Arrays.asList(new Object[] { "10.10.30.51" }));

    Assert.assertTrue(this.sampler.needProcess(r));

  }

  @Test
  public void testLogSamplerOutOfRangeBelow() {
    this.sampler = new LogSampler("ip:^(?:10\\.10|192\\.)", 23371, 23372);
    Map<String, List<Object>> r = new HashMap<String, List<Object>>();
    r.put("unused", Arrays.asList(new Object[] { "192.168.1.1" }));
    r.put("ip", Arrays.asList(new Object[] { "10.10.30.51" }));

    Assert.assertFalse(this.sampler.needProcess(r));

  }

  @Test
  public void testLogSamplerOutOfRangeAbove() {
    this.sampler = new LogSampler("ip:^(?:10\\.10|192\\.)", 23373, 23374);
    Map<String, List<Object>> r = new HashMap<String, List<Object>>();
    r.put("unused", Arrays.asList(new Object[] { "192.168.1.1" }));
    r.put("ip", Arrays.asList(new Object[] { "10.10.30.51" }));

    Assert.assertFalse(this.sampler.needProcess(r));

  }

  @Test
  public void testLogSamplerWrongRange() {
    this.sampler = new LogSampler("ip:^(?:10\\.10|192\\.)", 23372, 23372);
    Map<String, List<Object>> r = new HashMap<String, List<Object>>();
    r.put("unused", Arrays.asList(new Object[] { "192.168.1.1" }));
    r.put("ip", Arrays.asList(new Object[] { "10.10.30.51" }));

    Assert.assertFalse(this.sampler.needProcess(r));

  }

  @Test
  public void testLogSamplerFilterMatch() {
    this.sampler = new LogSampler("ip:^(?:10\\.10|192\\.)", 0, 0);
    Map<String, List<Object>> r = new HashMap<String, List<Object>>();
    r.put("unused", Arrays.asList(new Object[] { "172.10.1.1" }));
    r.put("ip", Arrays.asList(new Object[] { "192.168.1.1" }));

    Assert.assertTrue(this.sampler.needProcess(r));
  }

  @Test
  public void testLogSamplerFilterNotMatch() {
    this.sampler = new LogSampler("ip:^(?:10\\.10|192\\.)", 0, 0);
    Map<String, List<Object>> r = new HashMap<String, List<Object>>();
    r.put("unused", Arrays.asList(new Object[] { "192.168.1.1" }));
    r.put("ip", Arrays.asList(new Object[] { "172.10.1.1" }));

    Assert.assertFalse(this.sampler.needProcess(r));
  }

  @Test
  public void testLogSamplerFilterReverseBounds() {
    // same as filtering, but keep empty records
    this.sampler = new LogSampler("ip:^(?:10\\.10|192\\.)", 1, 0);
    Map<String, List<Object>> r = new HashMap<String, List<Object>>();
    r.put("unused", Arrays.asList(new Object[] { "192.168.1.1" }));
    r.put("ip", Arrays.asList(new Object[] { "" }));
    
    Assert.assertTrue(this.sampler.needProcess(r));
  }

  @Test
  public void testLogSamplerMatchReverseBounds() {
    // same as filtering, but keep empty records
    this.sampler = new LogSampler("ip:^(?:10\\.10|192\\.)", 1, 0);
    Map<String, List<Object>> r = new HashMap<String, List<Object>>();
    r.put("unused", Arrays.asList(new Object[] { "172.10.1.1" }));
    r.put("ip", Arrays.asList(new Object[] { "192.168.1.1" }));

    Assert.assertTrue(this.sampler.needProcess(r));
  }

  @Test
  public void testLogSamplerNoMatch() {
    this.sampler = new LogSampler("ip:^(?:10\\.10|192\\.)", 0, 16384);
    Map<String, List<Object>> r = new HashMap<String, List<Object>>();
    r.put("unused", Arrays.asList(new Object[] { "some 192.168.1.1" }));
    r.put("ip", Arrays.asList(new Object[] { "172.16.1.1" }));

    Assert.assertFalse(this.sampler.needProcess(r));

  }

  @Test
  public void testLogSamplerEmptyField() {
    this.sampler = new LogSampler("ip:^(?:10\\.10|192\\.)", 0, 1);
    Map<String, List<Object>> r = new HashMap<String, List<Object>>();
    r.put("unused", Arrays.asList(new Object[] { "192.168.1.1" }));
    r.put("ip", Arrays.asList(new Object[] { "" }));

    Assert.assertTrue(this.sampler.needProcess(r));
  }

  @Test
  public void testLogSamplerNullField() {
    this.sampler = new LogSampler("ip:^(?:10\\.10|192\\.)", 0, 32768);
    Map<String, List<Object>> r = new HashMap<String, List<Object>>();
    r.put("unused", Arrays.asList(new Object[] { "192.168.1.1" }));
    r.put("ip", Arrays.asList(new Object[] { null }));

    Assert.assertFalse(this.sampler.needProcess(r));
  }

  @Test
  public void testLogSamplerNullRecord() {
    this.sampler = new LogSampler("ip:^(?:10\\.10|192\\.)", 0, 32768);

    Assert.assertFalse(this.sampler.needProcess(null));
  }
}
