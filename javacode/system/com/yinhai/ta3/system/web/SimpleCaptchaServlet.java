package com.yinhai.ta3.system.web;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.captcha.Captcha;
import nl.captcha.backgrounds.BackgroundProducer;
import nl.captcha.backgrounds.FlatColorBackgroundProducer;
import nl.captcha.backgrounds.GradiatedBackgroundProducer;
import nl.captcha.backgrounds.SquigglesBackgroundProducer;
import nl.captcha.gimpy.BlockGimpyRenderer;
import nl.captcha.servlet.CaptchaServletUtil;
import nl.captcha.text.producer.DefaultTextProducer;
import nl.captcha.text.renderer.DefaultWordRenderer;
import nl.captcha.text.renderer.WordRenderer;

import com.yinhai.sysframework.config.SysConfig;

public class SimpleCaptchaServlet extends HttpServlet {

	private static final long serialVersionUID = -8876990808419095998L;
	protected int width = 150;
	protected int height = 50;
	protected boolean noise = false;
	protected String level = null;

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Integer checkCode = Integer.valueOf(Integer.parseInt(SysConfig.getSysConfig("checkCodeLevel", "1")));
		Captcha.Builder builder = new Captcha.Builder(width, height);
		List<Font> fontList = new ArrayList<Font>();
		fontList.add(new Font("Microsoft YaHei UI", 1, 40));
		fontList.add(new Font("Arial", 2, 40));

		int r = new Random().nextInt(255);
		int g = new Random().nextInt(255);
		int b = new Random().nextInt(255);

		WordRenderer wr = new DefaultWordRenderer(new Color(r, g, b), fontList);

		char[] chars = "23456789".toCharArray();
		BackgroundProducer bp = null;
		switch (checkCode.intValue()) {
		case 1:
			bp = new FlatColorBackgroundProducer(Color.LIGHT_GRAY);
			break;
		case 2:
			bp = new GradiatedBackgroundProducer();
			builder.addNoise();
			break;
		case 3:
			chars = "23456789abcdefgjkmxyzpqr".toCharArray();
			bp = new GradiatedBackgroundProducer();
			break;
		case 4:
			chars = "23456789abcdefgjkmxyzpqr".toCharArray();
			builder.addNoise();
			bp = new GradiatedBackgroundProducer();
			((GradiatedBackgroundProducer) bp).setFromColor(Color.black);
			((GradiatedBackgroundProducer) bp).setToColor(Color.white);
			break;
		case 5:
			chars = "23456789abcdefgjkmxyzpqr".toCharArray();
			bp = new SquigglesBackgroundProducer();
			break;
		case 6:
			builder.addNoise();
			chars = "23456789abcdefgjkmxyzpqr".toCharArray();
			bp = new SquigglesBackgroundProducer();
			break;
		default:
			bp = new FlatColorBackgroundProducer(Color.white);
		}

		bp = createCodeBackground(bp);
		builder.addText(new DefaultTextProducer(checkCode.intValue() / 2 + 3, chars), wr);
		builder.addBackground(bp);
		builder.gimp(new BlockGimpyRenderer(4));

		createCodeImgStyle(builder);
		Captcha captcha = builder.build();
		req.getSession().setAttribute("simpleCaptcha", captcha.getAnswer());
		CaptchaServletUtil.writeImage(resp, captcha.getImage());
	}

	protected BackgroundProducer createCodeBackground(BackgroundProducer bp) {
		return bp;
	}

	protected void createCodeImgStyle(Captcha.Builder builder) {
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
}
