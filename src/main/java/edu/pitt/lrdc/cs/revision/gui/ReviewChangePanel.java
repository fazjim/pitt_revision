package edu.pitt.lrdc.cs.revision.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.pitt.lrdc.cs.revision.model.ReviewDocument;

public class ReviewChangePanel extends JPanel {
	private ReviewDocument rd;
	private JList<String> reviewList;
	private JList<String> candidateList;
	private JTextPane detailPane;
	private JButton moveButton;
	private JButton reverseButton;

	private JButton confirmButton;
	private JButton cancelButton;

	private ArrayList<Integer> oldIndices;
	private ArrayList<Integer> newIndices;
	DefaultListModel<String> reviewListModel;
	DefaultListModel<String> candidateListModel;

	public ReviewChangePanel(ReviewDocument rd, ArrayList<Integer> oldIndices,
			ArrayList<Integer> newIndices) {
		this.rd = rd;

		this.oldIndices = oldIndices;
		this.newIndices = newIndices;
		moveButton = new JButton(">>>");
		reverseButton = new JButton("<<<");
		moveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				moveRight();
			}
		});
		reverseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				moveLeft();
			}
		});

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		buttonPanel.add(moveButton);
		buttonPanel.add(reverseButton);

		reviewListModel = new DefaultListModel<String>();
		candidateListModel = new DefaultListModel<String>();
		reviewList = new JList<String>(reviewListModel);
		candidateList = new JList<String>(candidateListModel);
		
		reviewList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				// TODO Auto-generated method stub
				showDetail(reviewList.getSelectedValue());
			}
		});
		candidateList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				// TODO Auto-generated method stub
				showDetail(candidateList.getSelectedValue());
			}
		});
		
		loadReviews();
		//reviewList.setListData(reviewStrs.toArray(new String[reviewStrs.size()]));
		reviewList.setVisibleRowCount(10);
		JScrollPane spReview = new JScrollPane(reviewList);
		JScrollPane spCandidate = new JScrollPane(candidateList);
		JPanel listPanel = new JPanel();
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.X_AXIS));
		listPanel.add(spReview);
		listPanel.add(buttonPanel);
		listPanel.add(spCandidate);

		detailPane = new JTextPane();

		JPanel confirmPanel = new JPanel();
		confirmButton = new JButton("Confirm");
		cancelButton = new JButton("Cancel");
		confirmButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				alignReviews();
				close();
				
			}
		});
		/*cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				cancel();
			}
		});*/
		confirmPanel.setLayout(new BoxLayout(confirmPanel, BoxLayout.X_AXIS));
		confirmPanel.add(confirmButton);
		//confirmPanel.add(cancelButton);

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(listPanel);
		this.add(detailPane);
		this.add(confirmPanel);
	}

	public void showDetail(String str) {
		detailPane.setText(str);
	}
	
	public void moveRight() {
		String value = reviewList.getSelectedValue();
		int index = reviewList.getSelectedIndex();
		reviewListModel.removeElementAt(index);
		//reviewList.remove(index);
		candidateListModel.addElement(value);
	}

	public void close() {
		JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
		topFrame.dispatchEvent(new WindowEvent(topFrame, WindowEvent.WINDOW_CLOSING));
	}
	
	public void moveLeft() {
		String value = candidateList.getSelectedValue();
		int index = candidateList.getSelectedIndex();
		candidateListModel.removeElementAt(index);
		//candidateList.remove(index);
		reviewListModel.addElement(value);
	}

	/*
	public void cancel() {
		DefaultListModel<String> values = (DefaultListModel<String>) reviewList
				.getModel();
		DefaultListModel<String> valuesToRemove = (DefaultListModel<String>) candidateList
				.getModel();
		for (int i = 0; i < valuesToRemove.size(); i++) {
			values.addElement(valuesToRemove.getElementAt(i));
		}
		valuesToRemove.clear();
	}*/

	public void alignReviews() {
		rd.clearReviews(oldIndices, newIndices);
		for(int i = 0;i<candidateListModel.getSize();i++) {
			String reviewStr = candidateListModel.getElementAt(i);
			int reviewNo = rd.getReviewNo(reviewStr);
			rd.addReview(-1, reviewNo, reviewStr, oldIndices, newIndices);
		}
	}
	
	public void loadReviews() {
		HashSet<String> reviewStrs = rd.getReviewStrs(oldIndices, newIndices);
		for(String reviewStr: reviewStrs) {
			candidateListModel.addElement(reviewStr);
		}
		ArrayList<String> restReviewStrs = rd.getReviewStrs();
		for(String restReview: restReviewStrs) {
			if(!reviewStrs.contains(restReview)) {
				reviewListModel.addElement(restReview);
			}
		}
		
	}
}
