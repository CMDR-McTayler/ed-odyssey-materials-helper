package nl.jixxed.eliteodysseymaterials.parser.messageprocessor;

import com.fasterxml.jackson.databind.JsonNode;
import nl.jixxed.eliteodysseymaterials.enums.StoragePool;
import nl.jixxed.eliteodysseymaterials.parser.AssetParser;
import nl.jixxed.eliteodysseymaterials.parser.DataParser;
import nl.jixxed.eliteodysseymaterials.parser.GoodParser;
import nl.jixxed.eliteodysseymaterials.service.StorageService;
import nl.jixxed.eliteodysseymaterials.service.event.BackpackEvent;
import nl.jixxed.eliteodysseymaterials.service.event.EventService;
import nl.jixxed.eliteodysseymaterials.service.event.StorageEvent;

public class BackpackMessageProcessor implements MessageProcessor {
    private static final AssetParser ASSET_PARSER = new AssetParser();
    private static final DataParser DATA_PARSER = new DataParser();
    private static final GoodParser GOOD_PARSER = new GoodParser();

    @Override
    public void process(final JsonNode journalMessage) {
        if (journalMessage.get("Items") == null || journalMessage.get("Components") == null || journalMessage.get("Data") == null) {
            EventService.publish(new BackpackEvent(journalMessage.get("timestamp").asText()));
            return;
        }
        StorageService.resetBackPackCounts();

        ASSET_PARSER.parse(journalMessage.get("Components").elements(), StoragePool.BACKPACK, StorageService.getAssets());
        GOOD_PARSER.parse(journalMessage.get("Items").elements(), StoragePool.BACKPACK, StorageService.getGoods());
        DATA_PARSER.parse(journalMessage.get("Data").elements(), StoragePool.BACKPACK, StorageService.getData());
        EventService.publish(new StorageEvent(StoragePool.BACKPACK));

    }
}
