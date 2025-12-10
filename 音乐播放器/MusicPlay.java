import java.io.*;
import javax.swing.text.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import java.util.Timer;

import javax.sound.sampled.*;

class audio {
	public boolean playFlag;
	private String musicPath; // 音频文件路径
	private AudioInputStream AudioStream;
	private AudioFormat AuFormat;
	private Clip clip;

	public void SetPlayAudioPath(String path) {
		this.musicPath = path;
		prefetch();
	}

	private void prefetch() {//读入音频文件流
		try {
			// 从File中获取音频输入流,File必须指向有效的音频文件,可能抛出 javax.sound.sampled.UnsupportedAudioFileException, java.io.IOException异常
			AudioStream = AudioSystem.getAudioInputStream(new File(musicPath));
			// 获取音频的编码对象
			AuFormat = AudioStream.getFormat();
			// 包装音频信息,AudioSystem.NOT_SPECIFIED可以表示文件大小
			DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, AuFormat, AudioSystem.NOT_SPECIFIED);
			// 使用包装音频信息后的Info类创建源数据行，充当混频器的源,注意DataLine.Info接受的是SourceDataLine,可以让AudioSystem.getLine(dataLineInfo)强制转化为SourceDataLine类型
			//可将音频数据写入缓冲区
			clip=AudioSystem.getClip();
			clip.open(AudioStream);

		} catch (UnsupportedAudioFileException e ) {
			e.printStackTrace();
		}
		catch ( LineUnavailableException  e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void playClip(int framePos) {
		if(clip == null)
			return;
		stop();
		clip.setFramePosition(framePos);
	}

	// 外部调用控制方法:生成音频主线程；
	public void play(int framePos) {
		playClip(framePos);
		clip.start();
	}

	public void stop() {
		if(clip != null && clip.isRunning()) {
			clip.stop();
		}
	}

	public void playAfterPause(long time) {
		if (clip != null) {
			clip.setMicrosecondPosition(time);
			clip.start();
		}
	}

	public long getFramePosition() {
		return clip != null ? clip.getMicrosecondPosition() : 0;
	}

	public void SetTime(long time) {
		if (clip != null) {
			clip.setMicrosecondPosition(time);
		}
	}

	public long GetTotaltime() {
		return clip != null ? clip.getMicrosecondLength() : 0;
	}

	public boolean getIsRunning() {
		return clip != null && clip.isRunning();
	}
}

class MyExtendsJFrame extends JFrame implements ActionListener, MouseListener {
	JLabel background; // 背景控件
	JButton buttonPlay; // 播放按钮
	JButton buttonOpenFile; // 打开文件按钮
	JTextPane textLyrics; // 歌词控件
	JLabel playTime; // 播放进度条控件
	audio audioPlay; // 播放类对象
	JList<String> listPlayFile; // 播放列表控件
	JButton buttonList; // 列表按钮
	Timer nTimer; // 定时器对象
	JButton buttonNext; // 下一首按钮
	JButton buttonPrev; // 上一首按钮
	JLabel backgroundPlay; // 播放图片
	JTextArea textMusic; // 音乐名
	JList<String> listPlayFileTime; // 音乐播放时间
	JButton buttonWay; // 播放方式按钮
	JTextArea TimeCount; // 显示当前播放时间
	JTextArea musictitle = new JTextArea(); // 歌曲名显示控件

	String MusicName; // 音乐名
	String playFile; // 文件路径
	String playFileName; // 文件名
	String playFileDirectory; // 文件具体目录
	int MusicTime; // 当前时间
	Vector<String> vt = new Vector<>(); // 播放队列
	Vector<String> vtime = new Vector<>(); // 时间队列
	int flagway = 1; // 播放方式：0为循环播放；1为顺序播放；2为随机播放
	boolean playFlag = false; // 是否正在播放

	// 新增成员变量
	private JScrollPane lyricsScrollPane;       // 歌词滚动面板
	private JPanel lyricsPanel;                // 歌词容器面板
	private ArrayList<JLabel> lyricLabels;     // 歌词标签集合
	private int currentLyricIndex = -1;        // 当前高亮歌词索引
	private int fontSize = 20;                 // 默认字体大小

	// 山外小楼夜听雨歌词
	String[] sLyrics1 = {
			"芙蓉花又栖满了枝头 \n",
			"亲何蝶雅留\n",
			"票白如江水向东流入\n",
			"望断门前隔岸的杨柳 \n",
			"寂寞仍不休\n",
			"我无言让眼泪长流\n",
			"我独酌山外小阁楼\n",
			"听一夜相思愁\n",
			"醉后让人烦忧心事雅收\n",
			"山外小阁楼我乘一叶小舟\n",
			"放思念随风漂流\n",
			"我独坐山外小阁楼\n",
			"窗外渔火如豆\n",
			"江畔晚风拂柳诉尽离愁\n",
			"当月色暖小楼是谁又在弹奏\n",
			"那一曲思念常留\n"
	};

	// 我和我的祖国
	String[] sLyrics2 = {
			"我和我的祖国\n",
			"一刻也不能分割\n",
			"无论我走到哪里\n",
			"都流出一首赞歌\n",
			"我歌唱每一座高山\n",
			"我歌唱每一条河\n",
			"袅袅炊烟，小小村落\n",
			"路上一道辙\n",
			"你用你那母亲的脉搏和我诉说\n"
	};

	// 烟雨易冷
	String[] sLyrics3 = {
			"听青春 迎来笑声 羡煞许多人\n",
			"那史册温柔不肯下笔都太狠\n",
			"烟花易冷 人事易分\n",
			"而你在问我是否还认真\n",
			"千年后 累世情深 还有谁在等\n",
			"而青史 岂能不真 魏书洛阳城\n",
			"如你在跟前世过门\n",
			"跟着红尘 跟随我浪迹一生\n",
			"雨纷纷 旧故里草木深\n",
			"我听闻 你始终一个人\n",
			"斑驳的城门 盘踞着老树根\n",
			"石板上回盪的是再等\n"
	};

	// 图片缩放方法（添加到 MyExtendsJFrame 类中）
	private ImageIcon resizeImageIcon(ImageIcon icon, int width, int height) {
		Image image = icon.getImage();
		Image resizedImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		return new ImageIcon(resizedImage);
	}

	public MyExtendsJFrame() {
		audioPlay = new audio();
		setTitle("播放器");
		setBounds(160, 100, 710, 430);
		setLayout(null);
		init();
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	void init() {
		try {
			Icon img = new ImageIcon(".//background.png");
			background = new JLabel(img);
			background.setBounds(0, 0, 700, 400);
			getLayeredPane().add(background, new Integer(Integer.MIN_VALUE));
			((JPanel) getContentPane()).setOpaque(false);

			buttonPlay = new JButton();
			buttonPlay.setBounds(322, 335, 40, 40);
			Icon icon = new ImageIcon(".//play.png");
			buttonPlay.setIcon(icon);
			buttonPlay.setBorderPainted(false);  // 不绘制边框
			buttonPlay.setContentAreaFilled(false);  // 不填充内容区域
			buttonPlay.setFocusPainted(false);  // 不绘制焦点状态
			buttonPlay.setOpaque(false);  // 设置为透明
			buttonPlay.addActionListener(this);
			add(buttonPlay);

			buttonOpenFile = new JButton("");
			buttonOpenFile.setBounds(440, 335, 40, 40);
			icon = new ImageIcon(".//open.png");
			buttonOpenFile.setIcon(icon);
			buttonOpenFile.setBorderPainted(false);  // 不绘制边框
			buttonOpenFile.setContentAreaFilled(false);  // 不填充内容区域
			buttonOpenFile.setFocusPainted(false);  // 不绘制焦点状态
			buttonOpenFile.setOpaque(false);  // 设置为透明
			buttonOpenFile.addActionListener(this);
			add(buttonOpenFile);

			buttonPrev = new JButton();
			buttonPrev.setBounds(282, 335, 40, 40);
			icon = new ImageIcon(".//prev.png");
			buttonPrev.setIcon(icon);
			buttonPrev.setBorderPainted(false);
			buttonPrev.setContentAreaFilled(false);
			buttonPrev.setFocusPainted(false);
			buttonPrev.setOpaque(false);
			buttonPrev.addActionListener(this);
			add(buttonPrev);

			buttonNext = new JButton();
			buttonNext.setBounds(362, 335, 40, 40);
			icon = new ImageIcon(".//next.png");
			buttonNext.setIcon(icon);
			buttonNext.setBorderPainted(false);
			buttonNext.setContentAreaFilled(false);
			buttonNext.setFocusPainted(false);
			buttonNext.setOpaque(false);
			buttonNext.addActionListener(this);
			add(buttonNext);

			buttonWay = new JButton();
			buttonWay.setBounds(402, 335, 40, 40);
			icon = new ImageIcon(".//loop.png");
			buttonWay.setIcon(icon);
			buttonWay.setBorderPainted(false);
			buttonWay.setContentAreaFilled(false);
			buttonWay.setFocusPainted(false);
			buttonWay.setOpaque(false);
			buttonWay.addActionListener(this);
			add(buttonWay);

			buttonList = new JButton();
			buttonList.setBounds(600, 335, 40, 40);
			icon = new ImageIcon(".//list.png");
			buttonList.setIcon(icon);
			buttonList.setBorderPainted(false);
			buttonList.setContentAreaFilled(false);
			buttonList.setFocusPainted(false);
			buttonList.setOpaque(false);
			buttonList.addActionListener(this);
			add(buttonList);

			icon = new ImageIcon(".//1.jpg");
			backgroundPlay = new JLabel(icon);
			backgroundPlay.setBounds(50, 50, 200, 200);
			getLayeredPane().add(backgroundPlay);

			TimeCount = new JTextArea("00:00");
			TimeCount.setBounds(10, 330, 40, 40);
			TimeCount.setForeground(Color.white);
			TimeCount.setOpaque(false);
			TimeCount.setEditable(false);
			add(TimeCount);

			listPlayFile = new JList<>();
			listPlayFile.setBounds(500, 50, 150, 200);
			listPlayFile.setOpaque(false);
			listPlayFile.setBackground(new Color(0, 0, 0, 0));
			listPlayFile.setForeground(Color.white);
			add(listPlayFile);
			listPlayFile.addMouseListener(this);

			listPlayFileTime = new JList<>();
			listPlayFileTime.setBounds(650, 50, 50, 200);
			listPlayFileTime.setOpaque(false);
			listPlayFileTime.setBackground(new Color(0, 0, 0, 0));
			listPlayFileTime.setForeground(Color.white);
			add(listPlayFileTime);

			textLyrics = new JTextPane();
			textLyrics.setBounds(300, 300, 200, 100);
			textLyrics.setForeground(Color.white);
			textLyrics.setOpaque(false);
			textLyrics.setEditable(false);
			add(textLyrics);

			initLyricsDisplay();

			icon = new ImageIcon(".//time.jpg");
			playTime = new JLabel(icon);
			playTime.setBounds(0, 324, 0, 3);
			add(playTime);

			musictitle.setBounds(300, 50, 200, 30);
			musictitle.setForeground(Color.white);
			musictitle.setFont(new Font("隶书", Font.PLAIN, 20)); // 设置隶书字体
			musictitle.setOpaque(false);
			musictitle.setEditable(false);
			add(musictitle);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 在init()方法中添加歌词显示初始化
	private void initLyricsDisplay() {
		// 创建歌词容器面板
		lyricsPanel = new JPanel();
		lyricsPanel.setLayout(new BoxLayout(lyricsPanel, BoxLayout.Y_AXIS));
		lyricsPanel.setOpaque(false);
		lyricLabels = new ArrayList<>();

		// 创建滚动面板 - 增大高度以显示更多歌词
		lyricsScrollPane = new JScrollPane(lyricsPanel);
		lyricsScrollPane.setBounds(250, 80, 290, 230);  // 高度增加到250
		lyricsScrollPane.setOpaque(false);
		lyricsScrollPane.getViewport().setOpaque(false);
		lyricsScrollPane.setBorder(null);
		// 设置垂直滚动条策略为始终不显示
		lyricsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		// 设置水平滚动条策略为始终不显示
		lyricsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		getLayeredPane().add(lyricsScrollPane);

		// 添加顶部和底部填充，使歌词居中
		lyricsPanel.add(Box.createVerticalStrut(100)); // 顶部填充
		// 歌词将在这里添加
		lyricsPanel.add(Box.createVerticalGlue());     // 底部填充
	}

	// 加载歌词到面板
	private void loadLyrics(String[] lyrics) {
		lyricsPanel.removeAll();
		lyricLabels.clear();

		// 添加顶部填充
		lyricsPanel.add(Box.createVerticalStrut(100));

		Font font = new Font("隶书", Font.PLAIN, fontSize);
		for (String line : lyrics) {
			JLabel label = new JLabel(line);
			label.setFont(font);
			label.setForeground(Color.WHITE);
			label.setAlignmentX(Component.CENTER_ALIGNMENT);
			lyricsPanel.add(label);
			lyricLabels.add(label);

			// 添加行间距
			lyricsPanel.add(Box.createVerticalStrut(10));
		}

		// 添加底部填充
		lyricsPanel.add(Box.createVerticalGlue());
		lyricsPanel.revalidate();
		lyricsPanel.repaint();

		// 初始滚动到顶部
		SwingUtilities.invokeLater(() -> {
			lyricsScrollPane.getVerticalScrollBar().setValue(0);
		});
	}

	// 更新歌词字体
	private void updateLyricsFont() {
		Font font = new Font("隶书", Font.PLAIN, fontSize);
		for (JLabel label : lyricLabels) {
			label.setFont(font);
		}
		lyricsPanel.revalidate();
	}



	// 高亮当前歌词并滚动
	private void highlightLyric(int index) {
		if (currentLyricIndex >= 0 && currentLyricIndex < lyricLabels.size()) {
			lyricLabels.get(currentLyricIndex).setForeground(Color.WHITE);
			lyricLabels.get(currentLyricIndex).setFont(new Font("隶书", Font.PLAIN, fontSize));
		}

		if (index >= 0 && index < lyricLabels.size()) {
			// 设置新高亮
			lyricLabels.get(index).setForeground(Color.YELLOW);
			lyricLabels.get(index).setFont(new Font("隶书", Font.BOLD, fontSize + 2));
			currentLyricIndex = index;

			// 计算滚动位置，使当前歌词居中
			Rectangle visibleRect = lyricsScrollPane.getViewport().getViewRect();
			Rectangle labelRect = lyricLabels.get(index).getBounds();

			// 计算目标位置：歌词居中
			int targetY = labelRect.y - (visibleRect.height - labelRect.height) / 2;

			// 确保不超出边界
			JScrollBar vertical = lyricsScrollPane.getVerticalScrollBar();
			targetY = Math.max(0, Math.min(targetY, vertical.getMaximum() - visibleRect.height));

			// 平滑滚动
			vertical.setValue(targetY);
		}
	}



	public void timerFun(int musicTime) { // 定时器函数
		if (nTimer != null) {
			nTimer.cancel();
		}
		nTimer = new Timer();
		nTimer.schedule(new TimerTask() {
			int PlayTime = 0;

			public void run() {
				long currentMicros = audioPlay.getFramePosition();
				PlayTime = (int) (currentMicros / 1_000_000);

				// 更新进度条宽度
				long totalTime = audioPlay.GetTotaltime();
				if (totalTime > 0) {
					playTime.setBounds(0, 324, (int) (((double) currentMicros / totalTime) * 600), 3);
				}

				// 时间显示
				int Second = PlayTime % 60;
				int Munite = PlayTime / 60;
				String sSecond = Second < 10 ? "0" + Second : Integer.toString(Second);
				String sMunite = Munite < 10 ? "0" + Munite : Integer.toString(Munite);
				TimeCount.setText(sMunite + ":" + sSecond);

				// 播放结束处理
				if (currentMicros >= audioPlay.GetTotaltime() && audioPlay.GetTotaltime() > 0) {
					nTimer.cancel();

					// 循环播放
					if (flagway == 0 && vt.size() != 0) {
						audioPlay.play(0);
						timerFun((int)(audioPlay.GetTotaltime() / 1_000_000));
					}

					// 顺序播放
					else if (flagway == 1 && vt.size() != 0) {
						int position = vt.indexOf(playFileName);
						position = (position + 1) % vt.size();
						playFileName = vt.get(position);
						playFile = playFileDirectory + playFileName;
						audioPlay.SetPlayAudioPath(playFile);
						audioPlay.play(0);
						timerFun((int)(audioPlay.GetTotaltime() / 1_000_000));
					}

					// 随机播放
					else if (flagway == 2 && vt.size() != 0) {
						int position = vt.indexOf(playFileName);
						int choose;
						do {
							choose = (int) (Math.random() * vt.size());
						} while (choose == position);
						playFileName = vt.get(choose);
						playFile = playFileDirectory + playFileName;
						audioPlay.SetPlayAudioPath(playFile);
						audioPlay.play(0);
						timerFun((int)(audioPlay.GetTotaltime() / 1_000_000));
					}
				}

				// 歌词显示
				textLyrics.setText(""); // 清空旧歌词

				// 确定是哪个歌曲的歌词
				int flagmusic = 0;
				if (playFileName != null && playFileName.equals("山外小楼夜听雨.wav")) {
					flagmusic = 1;
				} else if (playFileName != null && playFileName.equals("我和我的祖国.wav")) {
					flagmusic = 2;
				} else if (playFileName != null && playFileName.equals("烟花易冷.wav")) {
					flagmusic = 3;
				}

				SimpleAttributeSet attrSet = new SimpleAttributeSet();
				StyleConstants.setFontFamily(attrSet, "隶书");
				StyleConstants.setFontSize(attrSet, 20);

				try {
					Document doc = textLyrics.getDocument();

					ImageIcon originalIcon1 = new ImageIcon(".//1.jpg");
					ImageIcon originalIcon2 = new ImageIcon(".//2.jpg");
					ImageIcon originalIcon3 = new ImageIcon(".//3.jpg");
					ImageIcon resizedIcon1 = resizeImageIcon(
							originalIcon1,
							lyricsScrollPane.getWidth(),  // 面板宽度
							lyricsScrollPane.getHeight()  // 面板高度
					);
					ImageIcon resizedIcon2 = resizeImageIcon(
							originalIcon2,
							lyricsScrollPane.getWidth(),  // 面板宽度
							lyricsScrollPane.getHeight()  // 面板高度
					);
					ImageIcon resizedIcon3 = resizeImageIcon(
							originalIcon3,
							lyricsScrollPane.getWidth(),  // 面板宽度
							lyricsScrollPane.getHeight()  // 面板高度
					);

					if (flagmusic == 1) {
						int[] breaktime = {17, 21, 25, 30, 35, 39, 44, 49, 53, 59, 66, 72, 76, 81, 87, 94, 100};
						int position = -1;
						for (int i = 0; i < breaktime.length; i++) {
							if (PlayTime < breaktime[i]) {
								position = i - 1;
								break;
							}
						}

						// 首次加载歌词
						if (lyricLabels.isEmpty() || !backgroundPlay.getIcon().toString().contains("1.jpg")) {
							loadLyrics(sLyrics1);
							backgroundPlay.setIcon(new ImageIcon(".//1.jpg"));
						}

						highlightLyric(position);
					}

					else if (flagmusic == 2) {
						int[] breaktime = {1, 6, 10, 15, 19, 24, 28, 32, 50, 59};
						int position = -1;
						for (int i = 0; i < breaktime.length; i++) {
							if (PlayTime < breaktime[i]) {
								position = i - 1;
								break;
							}
						}

						// 首次加载歌词
						if (lyricLabels.isEmpty() || !backgroundPlay.getIcon().toString().contains("2.jpg")) {
							loadLyrics(sLyrics2);
							backgroundPlay.setIcon(new ImageIcon(".//2.jpg"));
						}

						highlightLyric(position);
					}

					else if (flagmusic == 3) {
						int[] breaktime = {5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60};
						int position = -1;
						for (int i = 0; i < breaktime.length; i++) {
							if (PlayTime < breaktime[i]) {
								position = i - 1;
								break;
							}
						}


						// 首次加载歌词
						if (lyricLabels.isEmpty() || !backgroundPlay.getIcon().toString().contains(".//3.jpg")) {
							loadLyrics(sLyrics3);
							backgroundPlay.setIcon(new ImageIcon(".//3.jpg"));
						}

						highlightLyric(position);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 0L, 100L);
	}

	@SuppressWarnings("unchecked")
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == buttonOpenFile) {
			FileDialog openFile = new FileDialog(this, "音乐文件夹", FileDialog.LOAD);
			openFile.setVisible(true);
			if (openFile.getFile() != null) {
				playFileName = openFile.getFile();
			} else {
				return;
			}

			this.playFileDirectory = openFile.getDirectory();
			playFile = playFileDirectory + playFileName;
			audioPlay.stop();

			audioPlay.SetPlayAudioPath(playFile);
			audioPlay.play(0);

			int iMusicTime = (int) (audioPlay.GetTotaltime() / 1_000_000);
			int iSecond = iMusicTime % 60;
			int iMinute = iMusicTime / 60;
			String formattedTime = String.format("%d:%02d", iMinute, iSecond);

			if (!vt.contains(playFileName)) {
				vt.add(playFileName);
				vtime.add(formattedTime);

				// 更新两个列表的数据
				listPlayFile.setListData(vt);
				listPlayFileTime.setListData(vtime);

				// 自动选择新添加的歌曲
				listPlayFile.setSelectedIndex(vt.size() - 1);
			}

			musictitle.setText(playFileName);
			Icon icon = new ImageIcon(".//stop.png");
			buttonPlay.setIcon(icon);
			backgroundPlay.setVisible(true);
			timerFun(iMusicTime);
		}

		if (e.getSource() == buttonPlay) {
			if (!audioPlay.getIsRunning()) {
				if (vt.size() != 0) {
					if (listPlayFile.getSelectedValue() != null) {
						playFile = playFileDirectory + listPlayFile.getSelectedValue().toString();
						playFileName = listPlayFile.getSelectedValue().toString();
					} else {
						playFile = playFileDirectory + listPlayFile.getModel().getElementAt(0).toString();
						playFileName = listPlayFile.getModel().getElementAt(0).toString();
						listPlayFile.setSelectedIndex(0);
					}
					audioPlay.stop();
					audioPlay.SetPlayAudioPath(playFile);
					audioPlay.play(0);
					Icon icon = new ImageIcon(".//stop.png");
					buttonPlay.setIcon(icon);
					backgroundPlay.setVisible(true);
					int nMusicTime = (int) (audioPlay.GetTotaltime() / 1_000_000);
					timerFun(nMusicTime);
				} else {
					JOptionPane.showMessageDialog(this, "没有音乐可以播放", "提示", JOptionPane.INFORMATION_MESSAGE);
				}
			} else {
				audioPlay.stop();
				if (nTimer != null) {
					nTimer.cancel();
				}
				Icon icon = new ImageIcon(".//play.png");
				buttonPlay.setIcon(icon);
			}
		}

		if (e.getSource() == buttonList) {
			if (listPlayFile.isVisible()) {
				listPlayFile.setVisible(false);
				listPlayFileTime.setVisible(false);
			} else {
				listPlayFile.setVisible(true);
				listPlayFileTime.setVisible(true);
			}
		}

		if (e.getSource() == buttonNext) {
			if (vt.size() != 0) {
				int position = vt.indexOf(playFileName);
				position = (position + 1) % vt.size();
				playFileName = vt.get(position);
				playFile = playFileDirectory + playFileName;
				musictitle.setText(playFileName);
				audioPlay.SetPlayAudioPath(playFile);
				audioPlay.play(0);
				int nMusicTime = (int) (audioPlay.GetTotaltime() / 1_000_000);
				timerFun(nMusicTime);
			}
		}

		if (e.getSource() == buttonPrev) {
			if (vt.size() != 0) {
				int position = vt.indexOf(playFileName);
				position = (vt.size() + position - 1) % vt.size();
				playFileName = vt.get(position);
				playFile = playFileDirectory + playFileName;
				musictitle.setText(playFileName);
				audioPlay.SetPlayAudioPath(playFile);
				audioPlay.play(0);
				int nMusicTime = (int) (audioPlay.GetTotaltime() / 1_000_000);
				timerFun(nMusicTime);
			}
		}

		if (e.getSource() == buttonWay) {
			if (flagway == 1) {
				flagway = 0;
				Icon icon = new ImageIcon(".//repeat.png");
				buttonWay.setIcon(icon);
			} else if (flagway == 0) {
				flagway = 2;
				Icon icon = new ImageIcon(".//rand.png");
				buttonWay.setIcon(icon);
			} else if (flagway == 2) {
				flagway = 1;
				Icon icon = new ImageIcon(".//loop.png");
				buttonWay.setIcon(icon);
			}
		}
	}

	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}

	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2 && e.getSource() == listPlayFile) {
			if (vt.size() != 0) {
				Object selected = listPlayFile.getSelectedValue();
				if (selected != null) {
					playFileName = selected.toString();
				} else {
					playFileName = listPlayFile.getModel().getElementAt(0).toString();
				}
				playFile = playFileDirectory + playFileName;
				audioPlay.SetPlayAudioPath(playFile);
				audioPlay.play(0);
				musictitle.setText(playFileName);
				Icon icon = new ImageIcon(".//stop.png");
				buttonPlay.setIcon(icon);
				backgroundPlay.setVisible(true);
				int nMusicTime = (int) (audioPlay.GetTotaltime() / 1_000_000);
				timerFun(nMusicTime);
			}
		}
	}
}

public class MusicPlay {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			MyExtendsJFrame frame = new MyExtendsJFrame();//创建音乐播放器程序窗口
		});
	}
}