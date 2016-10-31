package io.landysh.inflor.java.core.dataStructures;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.TreeMap;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.UninitializedMessageException;

import io.landysh.inflor.java.core.proto.FCSFrameProto.Message;
import io.landysh.inflor.java.core.proto.FCSFrameProto.Message.Keyword;
import io.landysh.inflor.java.core.proto.FCSFrameProto.Message.Transform;
import io.landysh.inflor.java.core.proto.FCSFrameProto.Message.Transform.Builder;
import io.landysh.inflor.java.core.transforms.AbstractTransform;
import io.landysh.inflor.java.core.transforms.BoundDisplayTransform;
import io.landysh.inflor.java.core.transforms.LogicleTransform;
import io.landysh.inflor.java.core.transforms.LogrithmicTransform;
import io.landysh.inflor.java.core.transforms.TransformType;
import io.landysh.inflor.java.core.utils.FCSUtils;
import io.landysh.inflor.java.core.proto.FCSFrameProto.Message.Dimension;


//don't use the default serializer, there is a protobuf spec.
@SuppressWarnings("serial")
public class FCSFrame extends DomainObject{

	private static final String DEFAULT_PREFFERED_NAME_KEYWORD = "$FIL";

	

	private TreeMap<String, FCSDimension> columnData;

	private HashMap<String, String> keywords;
	private String preferredName;
	private String compReference;

	// data properties
	private Integer rowCount = -1;
	// file details
	public String UUID;

	// minimal constructor, use with .load()
	public FCSFrame() {
		super(null);
	}

	/**
	 * Store keywords and numeric columns in a persistable object.
	 * 
	 * @param inKeywords
	 *            some annotation to get started with. Must be a valid FCS
	 *            header but may be added to later.
	 */
	public FCSFrame(HashMap<String, String> keywords, int rowCount) {
		this(null, keywords, rowCount);
	}
	
	public FCSFrame(String priorUUID, HashMap<String, String> keywords, int rowCount) {
		super(priorUUID);
		this.keywords = keywords;
		columnData = new TreeMap<String, FCSDimension>();
		this.rowCount = rowCount;
		preferredName = getKeywordValue(DEFAULT_PREFFERED_NAME_KEYWORD);
	}

	public void addColumn(String name, FCSDimension newDim) {
		if (rowCount == newDim.getSize()) {
			columnData.put(name, newDim);
		} else {
			throw new IllegalStateException("New dimension does not match frame size: " + rowCount.toString());
		}
	}

	public int getColumnCount() {
		return getColumnNames().length;
	}

	public String[] getColumnNames() {
		final int size = columnData.keySet().size();
		final String[] newArray = new String[size];
		final String[] columnNames = columnData.keySet().toArray(newArray);
		return columnNames;
	}

	public TreeMap<String, FCSDimension> getData() {
		return columnData;
	}

	public double[] getDimensionData(String displayName) {
		FCSDimension matchingDimension = FCSUtils.findCompatibleDimension(this, displayName);
		return matchingDimension.getData();
	}

	public HashMap<String, String> getKeywords() {
		return keywords;
	}

	public String getKeywordValue(String keyword) {
		String result = null;
		try {
			result = keywords.get(keyword).trim();
		} catch (NullPointerException npe) {
			// No operatoin, just return a null value.
		}
		return result;
	}


	public String getPrefferedName() {
		String name = UUID;
		if (this.preferredName != null) {
			name = this.preferredName;
		}
		return name;
	}

	public double[] getRow(int index) {
		final double[] row = new double[getColumnCount()];
		int i = 0;
		for (final String name : getColumnNames()) {
			row[i] = columnData.get(name).getData()[index];
			i++;
		}
		return row;
	}

	public int getRowCount() {
		return rowCount;
	}

	public FCSDimension getFCSDimension(String name) {
		return columnData.get(name);
	}

	public byte[] save() {
		// create the builder
		final Message.Builder messageBuilder = Message.newBuilder();
        messageBuilder.setId(ID);
		messageBuilder.setEventCount(this.rowCount);

		// add the dimension names.
		for (final String name : getColumnNames()) {
			messageBuilder.addDimNames(name);
		}

		// add the keywords.
		for (final String s : keywords.keySet()) {
			final String key = s;
			final String value = keywords.get(s);
			final Message.Keyword.Builder keyBuilder = Message.Keyword.newBuilder();
			keyBuilder.setKey(key);
			keyBuilder.setValue(value);
			final Message.Keyword newKeyword = keyBuilder.build();
			messageBuilder.addKeyword(newKeyword);
		}
		// add the FCS Dimensions.
		final Integer size = getColumnNames().length;
		for (int i = 0; i < size; i++) {
			final Message.Dimension.Builder dimBuilder = Message.Dimension.newBuilder();
			
			//Add required information.
			final String name = getColumnNames()[i];
			FCSDimension fcsdim = columnData.get(name);
			dimBuilder.setIndex(fcsdim.getIndex());
			dimBuilder.setPnn(fcsdim.getShortName());
			dimBuilder.setPns(fcsdim.getStainName());
			dimBuilder.setPneF1(fcsdim.getPNEF1());
			dimBuilder.setPneF2(fcsdim.getPNEF2());
			dimBuilder.setPnr(fcsdim.getRange());
			if (fcsdim.getCompRef()!=null){
	           dimBuilder.setCompRef(fcsdim.getCompRef());
			}
			dimBuilder.setId(fcsdim.ID);
			// Add the numeric data
			final double[] rawArray = columnData.get(name).getData();
			for (final double value : rawArray) {
				dimBuilder.addData(value);
			}

			try {
	           Builder tBuilder = buildTransform(fcsdim);
	           dimBuilder.setPreferredTransform(tBuilder.build());
			} catch (UninitializedMessageException e){
			  e.printStackTrace();
			}
			final Message.Dimension dim = dimBuilder.build();
			messageBuilder.addDimension(dim);
		}

		// build the message
		final Message buffer = messageBuilder.build();
		final byte[] bytes = buffer.toByteArray();
		return bytes;
	}

	private Builder buildTransform(FCSDimension fcsdim) {
    //Set the prefferred transformation.
    Message.Transform.Builder tBuilder = Message.Transform.newBuilder();
    AbstractTransform transform = fcsdim.getPreferredTransform();
    if (transform instanceof LogicleTransform){
      LogicleTransform logicle = (LogicleTransform) transform;
      tBuilder.setTransformType(TransformType.Logicle.toString());
      tBuilder.setLogicleT(logicle.getT());
      tBuilder.setLogicleW(logicle.getW());
      tBuilder.setLogicleM(logicle.getM());
      tBuilder.setLogicleA(logicle.getA());
    } else if (transform instanceof LogrithmicTransform) {
      LogrithmicTransform logTransform = (LogrithmicTransform) transform;
      tBuilder.setTransformType(TransformType.Logrithmic.toString());
      tBuilder.setLogMin(logTransform.getMinRawValue());
      tBuilder.setLogMax(logTransform.getMaxRawValue());
    } else if (transform instanceof BoundDisplayTransform) {
      tBuilder.setTransformType(TransformType.Bounded.toString());
      BoundDisplayTransform boundaryTransform = (BoundDisplayTransform) transform;
      tBuilder.setBoundMin(boundaryTransform.getMinRawValue());
      tBuilder.setLogMax(boundaryTransform.getMaxRawValue());
    }
    tBuilder.setId("FOO");//TODO Implement domainObject.
    return tBuilder;
  }

  public void save(FileOutputStream out) throws IOException {
		final byte[] message = this.save();
		out.write(message);
		out.flush();
	}
	
	public static FCSFrame load(byte[] bytes) throws InvalidProtocolBufferException {
		final Message loadedMessage = Message.parseFrom(bytes);

		// Load the keywords
		final HashMap<String, String> keywords = new HashMap<String, String>();
		for (int i = 0; i < loadedMessage.getKeywordCount(); i++) {
			final Keyword keyword = loadedMessage.getKeyword(i);
			final String key = keyword.getKey();
			final String value = keyword.getValue();
			keywords.put(key, value);
		}
		// Load the vectors
		final int rowCount = loadedMessage.getEventCount();
		final FCSFrame columnStore = new FCSFrame(loadedMessage.getId(), keywords, rowCount);
		final int dimCount = loadedMessage.getDimensionCount();
		final String[] dimen = new String[dimCount];
		for (int j = 0; j < dimCount; j++) {
			Dimension dim = loadedMessage.getDimension(j);
			String priorUUID = dim.getId();
			dimen[j] = priorUUID;
			final FCSDimension currentDimension = new FCSDimension(priorUUID, columnStore.getRowCount(), dim.getIndex(), dim.getPnn(), 
					dim.getPns(), dim.getPneF1(), dim.getPneF2(), dim.getPnr(), dim.getCompRef());
			for (int i = 0; i < currentDimension.getSize(); i++) {
				currentDimension.getData()[i] = dim.getData(i);
			}
			AbstractTransform preferredTransform = readTransform(dim);
			currentDimension.setPreferredTransform(preferredTransform);
			columnStore.addColumn(priorUUID, currentDimension);
		}
		columnStore.setPreferredName(columnStore.getKeywordValue(DEFAULT_PREFFERED_NAME_KEYWORD));
		return columnStore;
	}

	private static AbstractTransform readTransform(Dimension dim) {
    if (dim.hasPreferredTransform()){
      Transform tBuffer = dim.getPreferredTransform();
      if (tBuffer.getTransformType()==TransformType.Logicle.toString()){
        double t = tBuffer.getLogicleT();
        double w = tBuffer.getLogicleW();
        double m = tBuffer.getLogicleM();
        double a = tBuffer.getLogicleA();
        LogicleTransform transform = new LogicleTransform(t,w,m,a);
        return transform;
      } else if (tBuffer.getTransformType()==TransformType.Logrithmic.toString()){
        LogrithmicTransform transform = new LogrithmicTransform(tBuffer.getLogMin(), 
                                                                tBuffer.getLogMax());
        return transform;
        
      } else if (tBuffer.getTransformType()==TransformType.Bounded.toString()){
        BoundDisplayTransform transform = new BoundDisplayTransform(tBuffer.getBoundMin(), 
                                                                    tBuffer.getBoundMax());
        return transform;
      } else {
        //noop
      }
    }
    return null;
  }

  public static FCSFrame load(FileInputStream input) throws Exception {
		final byte[] buffer = new byte[input.available()];
		input.read(buffer);
		final FCSFrame columnStore = FCSFrame.load(buffer);
		return columnStore;
	}

	public void setData(TreeMap<String, FCSDimension> allData) {
		columnData = allData;
		rowCount = allData.get(getColumnNames()[0]).getData().length;
	}

	public void setPreferredName(String preferredName) {this.preferredName = preferredName;}

	@Override
	public String toString() {return getPrefferedName();}
	public void setCompRef(String id) {this.compReference = id;}
	public String getCompRef( ) {return this.compReference;}
}