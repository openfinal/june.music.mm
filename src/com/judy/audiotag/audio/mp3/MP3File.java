/**
 *  @author : Paul Taylor
 *  @author : Eric Farng
 *
 *  Version @version:$Id: MP3File.java,v 1.31 2007/11/26 14:20:28 paultaylor Exp $
 *
 *  MusicTag Copyright (C)2003,2004
 *
 *  This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser
 *  General Public  License as published by the Free Software Foundation; either version 2.1 of the License,
 *  or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 *  the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this library; if not,
 *  you can get a copy from http://www.opensource.org/licenses/lgpl-license.php or write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.judy.audiotag.audio.mp3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.logging.Level;

import javax.sound.sampled.UnsupportedAudioFileException;

import com.judy.audiotag.audio.AudioFile;
import com.judy.audiotag.audio.exceptions.CannotWriteException;
import com.judy.audiotag.audio.exceptions.InvalidAudioFrameException;
import com.judy.audiotag.audio.exceptions.ReadOnlyFileException;
import com.judy.audiotag.logging.AbstractTagDisplayFormatter;
import com.judy.audiotag.logging.PlainTextTagDisplayFormatter;
import com.judy.audiotag.logging.XMLTagDisplayFormatter;
import com.judy.audiotag.tag.Tag;
import com.judy.audiotag.tag.TagException;
import com.judy.audiotag.tag.TagNotFoundException;
import com.judy.audiotag.tag.TagOptionSingleton;
import com.judy.audiotag.tag.ape.APEv2Tag;
import com.judy.audiotag.tag.id3.AbstractID3v2Tag;
import com.judy.audiotag.tag.id3.AbstractTag;
import com.judy.audiotag.tag.id3.ID3v11Tag;
import com.judy.audiotag.tag.id3.ID3v1Tag;
import com.judy.audiotag.tag.id3.ID3v22Tag;
import com.judy.audiotag.tag.id3.ID3v23Tag;
import com.judy.audiotag.tag.id3.ID3v24Tag;
import com.judy.audiotag.tag.lyrics3.AbstractLyrics3;
import com.judy.audiotag.tag.lyrics3.Lyrics3v1;
import com.judy.audiotag.tag.lyrics3.Lyrics3v2;
import com.judy.momoplayer.util.Util;

/**
 * This class represents a physical MP3 File
 */
public class MP3File extends AudioFile {

	protected static AbstractTagDisplayFormatter tagFormatter;
	/**
	 * the ID3v2 tag that this file contains.
	 */
	private AbstractID3v2Tag id3v2tag = null;
	/**
	 * Representation of the idv2 tag as a idv24 tag
	 */
	private ID3v24Tag id3v2Asv24tag = null;
	/**
	 * The Lyrics3 tag that this file contains.
	 */
	private AbstractLyrics3 lyrics3tag = null;
	/**
	 * The ID3v1 tag that this file contains.
	 */
	private ID3v1Tag id3v1tag = null;
	/**
	 * Creates a new empty MP3File datatype that is not associated with a
	 * specific file.
	 */
	private APEv2Tag apev2Tag;

	public MP3File() {
	}

	/**
	 * Creates a new MP3File datatype and parse the tag from the given filename.
	 *
	 * @param filename
	 *            MP3 file
	 * @throws IOException
	 *             on any I/O error
	 * @throws TagException
	 *             on any exception generated by this library.
	 */
	public MP3File(String filename)
			throws IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException {
		this(new File(filename));
	}

	/* Load ID3V1tag if exists */
	public static final int LOAD_IDV1TAG = 2;

	/* Load ID3V2tag if exists */
	public static final int LOAD_IDV2TAG = 4;
	/**
	 * This option is currently ignored
	 */
	public static final int LOAD_LYRICS3 = 8;
	public static final int LOAD_APEV2TAG = 16;
	public static final int LOAD_ALL = LOAD_IDV1TAG | LOAD_IDV2TAG | LOAD_LYRICS3 | LOAD_APEV2TAG;

	/**
	 * Creates a new MP3File datatype and parse the tag from the given file
	 * Object, files must be writable to use this constructor.
	 *
	 * @param file
	 *            MP3 file
	 * @param loadOptions
	 *            decide what tags to load
	 * @throws IOException
	 *             on any I/O error
	 * @throws TagException
	 *             on any exception generated by this library.
	 */
	public MP3File(File file, int loadOptions)
			throws IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException {
		this(file, loadOptions, false);
	}

	/**
	 * Read v1 tag
	 *
	 * @param file
	 * @param newFile
	 * @param loadOptions
	 * @throws IOException
	 */
	private void readV1Tag(File file, RandomAccessFile newFile, int loadOptions) throws IOException {
		if ((loadOptions & LOAD_IDV1TAG) != 0) {
			log.finer("Attempting to read id3v1tags");
			try {
				id3v1tag = new ID3v11Tag(newFile,file.getName());
			} catch (TagNotFoundException ex) {
				log.info("No ids3v11 tag found");
			}

			try {
				if (id3v1tag == null) {
					id3v1tag = new ID3v1Tag(newFile,file.getName());
				}
			} catch (TagNotFoundException ex) {
				log.info("No id3v1 tag found");
			}
		}
	}

	/**
	 * Read V2tag if exists
	 * <p/>
	 * TODO:shouldnt we be handing TagExceptions:when will they be thrown
	 *
	 * @param file
	 * @param loadOptions
	 * @throws IOException
	 * @throws TagException
	 */
	private void readV2Tag(File file, int loadOptions) throws IOException, TagException {
		// We know where the Actual Audio starts so load all the file from start
		// to that point into
		// a buffer then we can read the IDv2 information without needing any
		// more file I/O
		int startByte = (int) ((MP3AudioHeader) audioHeader).getMp3StartByte();
		if (startByte >= AbstractID3v2Tag.TAG_HEADER_LENGTH) {
			log.finer("Attempting to read id3v2tags");
			FileInputStream fis = null;
			FileChannel fc = null;
			ByteBuffer bb = null;
			try {
				fis = new FileInputStream(file);
				fc = fis.getChannel();
				// Read into Byte Buffer
				bb = ByteBuffer.allocate(startByte);
				fc.read(bb);
			}catch(FileNotFoundException e){
				
			} finally {
				if (fc != null) {
					fc.close();
				}
				if (fis != null) {
					fis.close();
				}
			}
			//
			bb.rewind();
			if ((loadOptions & LOAD_IDV2TAG) != 0) {
				log.info("Attempting to read id3v2tags");
				try {
					this.setID3v2Tag(new ID3v24Tag(bb, file.getName()));
				} catch (TagNotFoundException ex) {
					log.info("No id3v24 tag found");
				}

				try {
					if (id3v2tag == null) {
						this.setID3v2Tag(new ID3v23Tag(bb, file.getName()));
					}
				} catch (TagNotFoundException ex) {
					log.info("No id3v23 tag found");
				}

				try {
					if (id3v2tag == null) {
						this.setID3v2Tag(new ID3v22Tag(bb, file.getName()));
					}
				} catch (TagNotFoundException ex) {
					log.info("No id3v22 tag found");
				}
			}
		} else {
			log.info("Not enough room for valid id3v2 tag:" + startByte);
		}
	}

	/**
	 * Read lyrics3 Tag
	 * <p/>
	 * TODO:not working
	 *
	 * @param file
	 * @param newFile
	 * @param loadOptions
	 * @throws IOException
	 */
	private void readLyrics3Tag(File file, RandomAccessFile newFile, int loadOptions) throws IOException {
		FileChannel fc;
		ByteBuffer byteBuffer = ByteBuffer.allocate(128);//TODO ???? 128 ? that is what
		if ((loadOptions & LOAD_LYRICS3) != 0) {
			try {
				fc = newFile.getChannel();
				fc.position(file.length() - 128);
				byteBuffer = ByteBuffer.allocate(128);
				fc.read(byteBuffer);
				byteBuffer.flip();
				lyrics3tag = new Lyrics3v2(byteBuffer);
				if (lyrics3tag == null) {
					lyrics3tag = new Lyrics3v1(byteBuffer);
				}
			} catch (TagNotFoundException ex) {
				
			}
		}
		 
	}

	/**
	 * Regets the auddio header starting from start of file, and write
	 * appropriate logging to indicate potential problem to user.
	 *
	 * @param startByte
	 * @param currentHeader
	 * @return
	 * @throws IOException
	 * @throws InvalidAudioFrameException
	 */
	private MP3AudioHeader checkAudioStart(long startByte, MP3AudioHeader currentHeader)
			throws IOException, InvalidAudioFrameException {
		MP3AudioHeader newAudioHeader;

		log.warning(file.getPath() + "ID3Tag ends at:" + startByte + ":but mp3audio doesnt start until:"
				+ currentHeader.getMp3StartByte());

		// because we cant agree on start location we reread the audioheader
		// from the start of the file, at least
		// this way we cant overwrite the audio although we might overwrtite
		// part of the tag if we write this file
		// back later
		newAudioHeader = new MP3AudioHeader(file, 0);
		if (currentHeader.getMp3StartByte() == newAudioHeader.getMp3StartByte()) {
			// Although the tag size appears to be incorrect at least we have
			// found the same location for the start
			// of audio whether we start searching from start of file or at the
			// end of the alleged of file
			log.warning(file.getPath() + "Using mp3audio start:" + newAudioHeader.getMp3StartByte());
		} else {
			// We get a different value if read from start, can't gurantee 100%
			// correct
			log.warning(file.getPath() + "Recalculated using mp3audio start:" + newAudioHeader.getMp3StartByte());
		}

		return newAudioHeader;
	}

	/**
	 * Creates a new MP3File datatype and parse the tag from the given file
	 * Object, files can be onpened read only if required.
	 *
	 * @param file
	 *            MP3 file
	 * @param loadOptions
	 *            decide what tags to load
	 * @param readOnly
	 *            causes the files to be opened readonly
	 * @throws IOException
	 *             on any I/O error
	 * @throws TagException
	 *             on any exception generated by this library.
	 */
	public MP3File(File file, int loadOptions, boolean readOnly)
			throws IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException {
		RandomAccessFile newFile = null;
		try {
			this.file = file;

			// Check File accessibility
			newFile = checkFilePermissions(file, readOnly);

			// Read ID3v2 tag size (if tag exists) to allow audioheader parsing
			// to skip over tag
			long startByte = AbstractID3v2Tag.getV2TagSizeIfExists(file);

			// If exception reading Mpeg then we should give up no point
			// continuing
			audioHeader = new MP3AudioHeader(file, startByte);

			if (startByte != ((MP3AudioHeader) audioHeader).getMp3StartByte()) {
				audioHeader = checkAudioStart(startByte, (MP3AudioHeader) audioHeader);
			}

			// Read v1 tags (if any)
			readV1Tag(file, newFile, loadOptions);

			// Read v2 tags (if any)
			readV2Tag(file, loadOptions);
			readAPEv2Tag(file, loadOptions);
			// If we have a v2 tag use that, if we dont but have v1 tag use that
			// otherwise use nothing
			// TODO:if have both should we merge
			// rather than just returning specific ID3v22 tag, would it be
			// better to return v24 version ?
			if (this.getID3v2Tag() != null) {
				tag = this.getID3v2Tag();
			} else if (id3v1tag != null) {
				tag = id3v1tag;
			}

			// Read Lyrics 3
			readLyrics3Tag(file, newFile, loadOptions);
		} finally {
			if (newFile != null) {
				newFile.close();
			}
		}
	}

	private void readAPEv2Tag(File file, int loadOptions) throws IOException {
		if ((loadOptions & LOAD_APEV2TAG) != 0) {
			try {
				log.log(Level.FINE, "尝试读取APEv2Tag");
				apev2Tag = new APEv2Tag(file);
			} catch (UnsupportedAudioFileException ex) {
				log.log(Level.INFO, "没有读到APEV2标签");
			}
		}
	}

	/**
	 * Used by tags when writing to calculate the location of the music file
	 *
	 * @return the location within the file that the audio starts
	 */
	public long getMP3StartByte(File file) throws InvalidAudioFrameException, IOException {
		try {
			// Read ID3v2 tag size (if tag exists) to allow audioheader parsing
			// to skip over tag
			long startByte = AbstractID3v2Tag.getV2TagSizeIfExists(file);

			MP3AudioHeader audioHeader = new MP3AudioHeader(file, startByte);
			if (startByte != audioHeader.getMp3StartByte()) {
				audioHeader = checkAudioStart(startByte, audioHeader);
			}
			return audioHeader.getMp3StartByte();
		} catch (InvalidAudioFrameException iafe) {
			throw iafe;
		} catch (IOException ioe) {
			throw ioe;
		}
	}

	/**
	 * Extracts the raw ID3v2 tag data into a file.
	 * <p/>
	 * This provides access to the raw data before manipulation, the data is
	 * written from the start of the file to the start of the Audio Data. This
	 * is primarily useful for manipulating corrupted tags that are not (fully)
	 * loaded using the standard methods.
	 *
	 * @param outputFile
	 *            to write the data to
	 * @return
	 * @throws TagNotFoundException
	 * @throws IOException
	 */
	public File extractID3v2TagDataIntoFile(File outputFile) throws TagNotFoundException, IOException {
		int startByte = (int) ((MP3AudioHeader) audioHeader).getMp3StartByte();
		if (startByte >= 0) {

			// Read byte into buffer
			FileInputStream fis = new FileInputStream(file);
			FileChannel fc = fis.getChannel();
			ByteBuffer bb = ByteBuffer.allocate(startByte);
			fc.read(bb);

			// Write bytes to outputFile
			FileOutputStream out = new FileOutputStream(outputFile);
			out.write(bb.array());
			out.close();
			fc.close();
			fis.close();
			return outputFile;
		}
		throw new TagNotFoundException("There is no ID3v2Tag data in this file");
	}

	/**
	 * Return audio header
	 */
	public MP3AudioHeader getMP3AudioHeader() {
		return (MP3AudioHeader) getAudioHeader();
	}

	/**
	 * Returns true if this datatype contains an <code>Id3v1</code> tag
	 *
	 * @return true if this datatype contains an <code>Id3v1</code> tag
	 */
	public boolean hasID3v1Tag() {
		return (id3v1tag != null);
	}

	public boolean hasAPEv2Tag() {
		return apev2Tag != null;
	}

	/**
	 * Returns true if this datatype contains an <code>Id3v2</code> tag
	 *
	 * @return true if this datatype contains an <code>Id3v2</code> tag
	 */
	public boolean hasID3v2Tag() {
		return (id3v2tag != null);
	}

	/**
	 * Returns true if this datatype contains a <code>Lyrics3</code> tag TODO
	 * disabled until Lyrics3 fixed
	 * 
	 * @return true if this datatype contains a <code>Lyrics3</code> tag
	 */
	/*
	 * public boolean hasLyrics3Tag() { return (lyrics3tag != null); }
	 */
	/**
	 * Creates a new MP3File datatype and parse the tag from the given file
	 * Object.
	 *
	 * @param file
	 *            MP3 file
	 * @throws IOException
	 *             on any I/O error
	 * @throws TagException
	 *             on any exception generated by this library.
	 */
	public MP3File(File file) throws IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException {
		this(file, LOAD_ALL);
	}

	/**
	 * Sets the ID3v1(_1)tag to the tag provided as an argument.
	 *
	 * @param id3v1tag
	 */
	public void setID3v1Tag(ID3v1Tag id3v1tag) {
		log.info("setting tagv1:v1 tag");
		this.id3v1tag = id3v1tag;
	}

	public void setID3v1Tag(Tag id3v1tag) {
		log.info("setting tagv1:v1 tag");
		this.id3v1tag = (ID3v1Tag) id3v1tag;
	}

	public void setAPEv2Tag(APEv2Tag tag) {
		log.log(Level.INFO, "设置了apev2的tag");
		this.apev2Tag = tag;
	}

	/**
	 * Sets the <code>ID3v1</code> tag for this datatype. A new
	 * <code>ID3v1_1</code> datatype is created from the argument and then used
	 * here.
	 *
	 * @param mp3tag
	 *            Any MP3Tag datatype can be used and will be converted into a
	 *            new ID3v1_1 datatype.
	 */
	public void setID3v1Tag(AbstractTag mp3tag) {
		log.info("setting tagv1:abstract");
		id3v1tag = new ID3v11Tag(mp3tag);
	}

	/**
	 * Returns the <code>ID3v1</code> tag for this datatype.
	 *
	 * @return the <code>ID3v1</code> tag for this datatype
	 */
	public ID3v1Tag getID3v1Tag() {
		return id3v1tag;
	}

	public APEv2Tag getAPEv2Tag() {
		return apev2Tag;
	}

	/**
	 * Sets the <code>ID3v2</code> tag for this datatype. A new
	 * <code>ID3v2_4</code> datatype is created from the argument and then used
	 * here.
	 *
	 * @param mp3tag
	 *            Any MP3Tag datatype can be used and will be converted into a
	 *            new ID3v2_4 datatype.
	 */
	public void setID3v2Tag(AbstractTag mp3tag) {
		id3v2tag = new ID3v24Tag(mp3tag);

	}

	/**
	 * Sets the v2 tag to the v2 tag provided as an argument. Also store a v24
	 * version of tag as v24 is the interface to be used when talking with
	 * client applications.
	 *
	 * @param id3v2tag
	 */
	public void setID3v2Tag(AbstractID3v2Tag id3v2tag) {
		this.id3v2tag = id3v2tag;
		if (id3v2tag instanceof ID3v24Tag) {
			this.id3v2Asv24tag = (ID3v24Tag) this.id3v2tag;
		} else {
			this.id3v2Asv24tag = new ID3v24Tag(id3v2tag);
		}
	}

	/**
	 * Set v2 tag ,dont need to set v24 tag because saving
	 * <p/>
	 * TODO temp its rather messy
	 */
	public void setID3v2TagOnly(AbstractID3v2Tag id3v2tag) {
		this.id3v2tag = id3v2tag;
		this.id3v2Asv24tag = null;
	}

	/**
	 * Returns the <code>ID3v2</code> tag for this datatype.
	 *
	 * @return the <code>ID3v2</code> tag for this datatype
	 */
	public AbstractID3v2Tag getID3v2Tag() {
		return id3v2tag;
	}

	/**
	 *
	 * @return a representation of tag as v24
	 */
	public ID3v24Tag getID3v2TagAsv24() {
		return id3v2Asv24tag;
	}

	/**
	 * Sets the <code>Lyrics3</code> tag for this datatype. A new
	 * <code>Lyrics3v2</code> datatype is created from the argument and then
	 * used here.
	 *
	 * @param mp3tag
	 *            Any MP3Tag datatype can be used and will be converted into a
	 *            new Lyrics3v2 datatype.
	 */
	/*
	 * public void setLyrics3Tag(AbstractTag mp3tag) { lyrics3tag = new
	 * Lyrics3v2(mp3tag); }
	 */
	/**
	 *
	 *
	 * @param lyrics3tag
	 */
	/*
	 * public void setLyrics3Tag(AbstractLyrics3 lyrics3tag) { this.lyrics3tag =
	 * lyrics3tag; }
	 */
	/**
	 * Returns the <code>ID3v1</code> tag for this datatype.
	 *
	 * @return the <code>ID3v1</code> tag for this datatype
	 */

	/*
	 * public AbstractLyrics3 getLyrics3Tag() { return lyrics3tag; }
	 */
	/**
	 * Remove tag from file
	 *
	 * @param mp3tag
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void delete(AbstractTag mp3tag) throws FileNotFoundException, IOException {
		mp3tag.delete(new RandomAccessFile(this.file, "rw"));
	}

	/**
	 * Saves the tags in this datatype to the file referred to by this datatype.
	 *
	 * @throws IOException
	 *             on any I/O error
	 * @throws TagException
	 *             on any exception generated by this library.
	 */
	public void save() throws IOException, TagException {
		save(this.file);
	}

	/**
	 * Overriden for comptability with merged code
	 *
	 * @throws CannotWriteException
	 */
	public void commit() throws CannotWriteException {
		try {
			save();
		} catch (IOException ioe) {
			throw new CannotWriteException(ioe);
		} catch (TagException te) {
			throw new CannotWriteException(te);
		}
	}

	/**
	 * Saves the tags in this datatype to the file argument. It will be saved as
	 * TagConstants.MP3_FILE_SAVE_WRITE
	 *
	 * @param file
	 *            file to save the this datatype's tags to
	 * @throws FileNotFoundException
	 *             if unable to find file
	 * @throws IOException
	 *             on any I/O error
	 */
	public void save(File file) throws FileNotFoundException, IOException {
		log.info("Saving  : " + file.getAbsolutePath());
		RandomAccessFile rfile = null;
		try {
			// ID3v2 Tag
			if (TagOptionSingleton.getInstance().isId3v2Save()) {
				if (id3v2tag == null) {
					rfile = new RandomAccessFile(file, "rw");
					// 这里固定用ID3v23格式来删除,所以写入的时候也必须同时写入
					new ID3v23Tag().delete(rfile);
					rfile.close();
				} else {
					id3v2tag.write(file, ((MP3AudioHeader) this.getAudioHeader()).getMp3StartByte());
				}
			}
			rfile = new RandomAccessFile(file, "rw");

			// Lyrics 3 Tag
			if (TagOptionSingleton.getInstance().isLyrics3Save()) {
				if (lyrics3tag != null) {
					lyrics3tag.write(rfile);
				}
			}
			// ID3v1 tag
			if (TagOptionSingleton.getInstance().isId3v1Save()) {
				log.info("saving v1");
				if (id3v1tag == null) {
					log.info("deleting v1");
					(new ID3v1Tag()).delete(rfile);
				} else {
					log.info("saving v1 still");
					id3v1tag.write(rfile);
				}
			}
			if (apev2Tag == null) {
				new APEv2Tag().delete(rfile, id3v1tag != null);
			} else {
				apev2Tag.write(rfile, id3v1tag != null);
			}
		} catch (FileNotFoundException ex) {
			log.log(Level.SEVERE, file.getAbsolutePath() + ":Problem writing tags to file,FileNotFoundException",
					ex.getMessage());
			throw ex;
		} catch (IOException iex) {
			log.log(Level.SEVERE, file.getAbsolutePath() + ":Problem writing tags to file,IOException",
					iex.getMessage());
			throw iex;
		} catch (RuntimeException re) {
			log.log(Level.SEVERE, file.getAbsolutePath() + ":Problem writing tags to file,RuntimeException",
					re.getMessage());
			throw re;
		} finally {
			if (rfile != null) {
				rfile.close();
			}
		}
	}

	/**
	 * Displays MP3File Structure
	 */
	public String displayStructureAsXML() {
		createXMLStructureFormatter();
		MP3File.tagFormatter.openHeadingElement("file", this.getFile().getAbsolutePath());
		if (this.getID3v1Tag() != null) {
			this.getID3v1Tag().createStructure();
		}
		if (this.getID3v2Tag() != null) {
			this.getID3v2Tag().createStructure();
		}
		MP3File.tagFormatter.closeHeadingElement("file");
		return tagFormatter.toString();
	}

	/**
	 * Displays MP3File Structure
	 */
	public String displayStructureAsPlainText() {
		createPlainTextStructureFormatter();
		MP3File.tagFormatter.openHeadingElement("file", this.getFile().getAbsolutePath());
		if (this.getID3v1Tag() != null) {
			this.getID3v1Tag().createStructure();
		}
		if (this.getID3v2Tag() != null) {
			this.getID3v2Tag().createStructure();
		}
		MP3File.tagFormatter.closeHeadingElement("file");
		return tagFormatter.toString();
	}

	private static void createXMLStructureFormatter() {
		tagFormatter = new XMLTagDisplayFormatter();
	}

	private static void createPlainTextStructureFormatter() {
		tagFormatter = new PlainTextTagDisplayFormatter();
	}

	public static AbstractTagDisplayFormatter getStructureFormatter() {
		return tagFormatter;
	}

	/**
	 * Set the Tag
	 *
	 * If the parameter tag is a v1tag then the v1 tag is set if v2tag then the
	 * v2tag.
	 * 
	 * @param tag
	 */
	public void setTag(Tag tag) {
		this.tag = tag;
		if (tag instanceof ID3v1Tag) {
			setID3v1Tag((ID3v1Tag) tag);
		} else {
			setID3v2Tag((AbstractID3v2Tag) tag);
		}

	}

	public static void main(String[] args) throws Exception {
		MP3File mp = new MP3File(new File("D:\\难道爱一个人有错吗.mp3"), 0);
		// ID3v1Tag tag = mp.getID3v1Tag();
		// System.out.println(Util.convertString(tag.getFirstArtist(), "GBK"));
		// System.out.println(Util.convertString(tag.getFirstTitle()));
		// String s= mp.displayStructureAsPlainText();
		// System.out.println(s);
		ID3v23Tag v2 = new ID3v23Tag();
		v2.addAlbum(new String("V2专辑名".getBytes("GBK"), "ISO8859-1"));
		v2.setArtist(new String("V2艺术家".getBytes("GBK"), "ISO8859-1"));
		v2.setComment(new String("V2的注释".getBytes("GBK"), "ISO8859-1"));
		v2.setTitle(new String("v2有没有人告诉你".getBytes("GBK"), "ISO8859-1"));
		v2.setGenre(new String("v2风格很怪".getBytes("GBK"), "ISO8859-1"));
		v2.setYear("2008");
		v2.setTrack("HELLO");
		mp.setID3v2Tag(v2);
		ID3v1Tag v1 = new ID3v1Tag();
		v1.setAlbum(Util.convertString("V1的专辑名", "GBK", "ISO8859-1"));
		v1.setArtist(Util.convertString("V1的东来来往", "GBK", "ISO8859-1"));
		v1.setComment(Util.convertString("V2的注释在这里呢", "GBK", "ISO8859-1"));
		v1.setTitle(Util.convertString("V1忘记你是我的错", "GBK", "ISO8859-1"));
		v1.setGenre(Util.convertString("V1流行", "GBK", "ISO8859-1"));
		v1.setYear("2007");
		mp.setID3v1Tag(v1);
		mp.save();

	}
}
