package net.buildabrowser.babbrowser.browser.render.content.flow.floatbox;

import java.util.List;

import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.FlowFragment;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;

public interface FloatTracker {
	
	// Boolean return - if we have a non-0 reservedWidth, it could be hard to determine where the next line is
	// so just let the caller handle it for now
	boolean addLineStartFloat(FlowFragment box, LayoutConstraint lineConstraint, int reservedWidth);

	// Line constraint is a hacky way to determine where the box will start
	boolean addLineEndFloat(FlowFragment box, LayoutConstraint lineConstraint, int reservedWidth);

	int clearedLineStartPosition();

	int clearedLineEndPosition();

	// TODO: Optimize repeatedly getting the same value
	int lineStartPos();

	int lineEndPos(LayoutConstraint lineConstraint);

	void reset();

	List<FlowFragment> allFloats();

	// Kind of hacky way to track int positions
	void adjustPos(int x, int y);

	// More hacky stuff for positioning - if we layout a block with a fixed height,
	// the end y may increase by a different amount than the sum of lines inside
	int mark();

	void restoreMark(int mark);

	// Exists to min-bound the containing block
	int contentHeight();
	
	static FloatTracker create() {
		return new FloatTrackerImp();
	}

}