// Import the necessary libraries
import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.VideoQuality
import org.jsoup.Jsoup

// Define the class that implements MainAPI
class Braflix : MainAPI() {
    // Change the name, mainUrl, and icon properties
    override val name: String = "Braflix"
    override val mainUrl: String = "https://www.braflix.app"
    override val icon: String = "https://www.braflix.app/assets/images/logo.png"

    // Override the search method
    override fun search(query: String): ArrayList<SearchResponse> {
        // Create an empty list to store the results
        val results = ArrayList<SearchResponse>()

        // Perform a web search on the site using the query
        val response = khttp.get("$mainUrl/search?q=$query")

        // Parse the HTML of the response
        val document = Jsoup.parse(response.text)

        // Find all the elements that contain the search results
        val elements = document.select("div.card")

        // Loop through the elements
        for (element in elements) {
            // Extract the title, poster, and url of each result
            val title = element.selectFirst("h5.card-title").text()
            val poster = element.selectFirst("img.card-img-top").attr("src")
            val url = element.selectFirst("a.card-link").attr("href")

            // Create a SearchResponse object and add it to the list
            results.add(SearchResponse(title, url, this.name, TvType.TvSeries, poster, null, null))
        }

        // Return the list of results
        return results
    }

    // Override the load method
    override fun load(url: String): LoadResponse {
        // Create an empty list to store the video links
        val videoLinks = ArrayList<ExtractorLink>()

        // Get the HTML of the url
        val response = khttp.get(url)

        // Parse the HTML of the response
        val document = Jsoup.parse(response.text)

        // Find the element that contains the video iframe
        val iframe = document.selectFirst("iframe.embed-responsive-item")

        // Extract the src attribute of the iframe
        val src = iframe.attr("src")

        // Create an ExtractorLink object and add it to the list
        videoLinks.add(ExtractorLink(name, name, src, url, Qualities.Unknown.value))

        // Extract the title, poster, and description of the video
        val title = document.selectFirst("h1").text()
        val poster = document.selectFirst("img.img-fluid").attr("src")
        val description = document.selectFirst("p").text()

        // Create a LoadResponse object and return it
        return LoadResponse(title, url, this.name, TvType.TvSeries, videoLinks, poster, description, null, null, null)
    }

    // Optionally, override the getMainPage method
    override fun getMainPage(): HomePageResponse {
        // Create an empty list to store the home page lists
        val homePageList = ArrayList<HomePageList>()

        // Get the HTML of the main url
        val response = khttp.get(mainUrl)

        // Parse the HTML of the response
        val document = Jsoup.parse(response.text)

        // Find all the elements that contain the home page sections
        val elements = document.select("div.row")

        // Loop through the elements
        for (element in elements) {
            // Extract the name of the section
            val name = element.selectFirst("h3").text()

            // Create an empty list to store the titles
            val titles = ArrayList<Title>()

            // Find all the elements that contain the titles
            val subElements = element.select("div.card")

            // Loop through the sub-elements
            for (subElement in subElements) {
                // Extract the title, poster, and url of each title
                val title = subElement.selectFirst("h5.card-title").text()
                val poster = subElement.selectFirst("img.card-img-top").attr("src")
                val url = subElement.selectFirst("a.card-link").attr("href")

                // Create a Title object and add it to the list
                titles.add(Title(title, url, this.name, TvType.TvSeries, poster, null, null))
            }

            // Create a HomePageList object and add it to the list
            homePageList.add(HomePageList(name, titles))
        }

        // Return a HomePageResponse object
        return HomePageResponse(homePageList)
    }
}