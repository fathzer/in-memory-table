package com.fathzer.imt.implementation;

import com.fathzer.imt.AbstractBitmapTest;
import com.fathzer.imt.TagsTableFactory;
import com.fathzer.imt.implementation.SimpleTagsTableFactory;

public class EWAHBitmapTest extends AbstractBitmapTest {
	@Override
	protected TagsTableFactory<? extends Object> buildFactory() {
		return SimpleTagsTableFactory.EWAH_FACTORY;
	}
}
